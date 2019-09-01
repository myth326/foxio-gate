package cn.foxio.gate.tcp.websocket;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.tcp.data.FoxPlayerStatus;
import cn.foxio.gate.tcp.data.HeartbeatData;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.data.OriginalPackage;
import cn.foxio.gate.tcp.gateway.FoxGatewayAccepter;
import cn.foxio.gate.tools.NettyUtils;
import cn.foxio.gate.tools.TimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * websocket server handler
 * @author lucky
 *
 */
public class FoxWsGatewayServerHandler extends SimpleChannelInboundHandler<Object> {

	private Logger logger = Logger.getLogger(FoxWsGatewayServerHandler.class);


	
    private  byte [] HEARTBEAT_SEQUENCE = new byte[1];

    
	

	private FoxGatewayAccepter accepter;

	private WebSocketServerHandshaker handshaker;

	public FoxWsGatewayServerHandler() {
		HEARTBEAT_SEQUENCE = new HeartbeatData().toByteArrays();
	}

	public FoxWsGatewayServerHandler(FoxGatewayAccepter accepter) {
		this.accepter = accepter;
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		
		//子服不做心跳检测
		if (evt instanceof IdleStateEvent  && ! NettyUtils.isService(ctx) ) {
	        IdleStateEvent e = (IdleStateEvent) evt;
	        if ( IdleState.ALL_IDLE == e.state() && ! NettyUtils.isService(ctx) ) {
	        	long uid = NettyUtils.getUserId(ctx);
	        	logger.info( NettyUtils.getKey(ctx) +":: connectid = " + uid+ ":: " +e.state()  ); 
	        	logger.info("闲置时间过长断线 ctx = " + NettyUtils.getKey(ctx) );
	        	accepter.interrupt(ctx , uid );
	        }
	        
	    }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		logger.info("客户端断开连接 :  " + NettyUtils.getKey(ctx));

		accepter.interrupt(ctx, 0L);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = insocket.getAddress().getHostAddress();
		int port = insocket.getPort();
		String key = ip + ":" + port;
		System.out.println("create client "+key);
		NettyUtils.setKey(ctx, key);
		NettyUtils.autoRegUid(ctx);
		NettyUtils.regClient(ctx);
		
		
		insocket = (InetSocketAddress) ctx.channel().localAddress();
		String ip2 = insocket != null ? insocket.getAddress().getHostAddress() : "null";
		
		logger.info( "localAddress::" + ip2 + " , remoteAddress::" + ip  );
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		
		NettyUtils.setProto(ctx, NettyUtils.WEB_SOCKET);

		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		if (insocket != null) {
			String ip = insocket.getAddress().getHostAddress();
			int port = insocket.getPort();
			String key = ip + ":" + port;
			logger.info(getClass().getSimpleName() + "::" + "创建连接" + key);
		} else {
			System.out.println("创建连接但， " + getClass().getSimpleName() + " ctx.channel().remoteAddress() = null ");
		}
		
		NettyUtils.sendMsgToCtx(ctx, HEARTBEAT_SEQUENCE, 0);

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		long uid = NettyUtils.getUserId(ctx);
		System.err.println("正常断线  wsgate ctx = " + NettyUtils.getKey(ctx) + " time  " + TimeUtil.getDateFormat() );
		accepter.interrupt(ctx , uid );
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, ((FullHttpRequest) msg));
		} else if (msg instanceof WebSocketFrame) {
			handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

		// 判断是否关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}

		// 判断是否ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}else if (frame instanceof TextWebSocketFrame) {
			String msg = "主动断开连接, 因为错误的通讯格式 TextWebSocketFrame ， ip:port " + NettyUtils.getKey(ctx);
			logger.error(msg);
			System.err.println(msg);
			NettyUtils.closeCtxConnect(ctx);
			return;
		}

		BinaryWebSocketFrame binary = (BinaryWebSocketFrame) frame;

		ByteBuf bf = binary.content();

		byte[] datas = new byte[bf.readableBytes()];
		bf.readBytes(datas);
		
		//System.out.println( Arrays.toString(datas));
		
		
		
		OriginalPackage omsg = new OriginalPackage();
		omsg.readBytes(datas);
		
		InnerMessage box = new InnerMessage(String.valueOf(omsg.getCmdId() / 100), NettyUtils.getTableId(ctx), omsg.getProtoData() );
		box.setCmdId(omsg.getCmdId());
		box.setType(MessageTypeDef.ClientToService.getValue());
		box.setCustomerId(NettyUtils.getCustomerId(ctx));
		
		if (box.getCmdId() != MessageTypeDef.HeartBeat.getValue() ) {
			
			//自动补充客户端发布的消息规则
			FoxPlayerStatus cps = NettyUtils.getPlayerStatus(ctx);
			String key = NettyUtils.autoKey(ctx);
			box.setKey(key);
			box.setUserId( cps.getUserId() );
			box.setMainId( String.valueOf( box.getCmdId() / 100));
			box.setSubId(cps.getTableId());
			
			logger.info("gateway 收到client消息! "+key+" msg = " + box.getCmdId());
			if ( box.getCmdId() == MessageTypeDef.Handshake.getValue() ) {
				return;
			}
			box.setType( MessageTypeDef.ClientToService.getValue());
			accepter.acceptMsg(box, ctx);
		}else{
			//System.out.println("收到心跳并回复" + NettyUtils.getKey(ctx) +" , uid = "+ NettyUtils.getUserId(ctx)+ " time = "+ TimeUtil.getDateFormat(new Date()));
			//NettyUtils.sendMsgToCtx( ctx , HEARTBEAT_SEQUENCE , MessageTypeDef.HeartBeat.getValue() );
		}

	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

		//!"websocket".equals(req.headers().get(HttpHeaderNames.UPGRADE)
		if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, req,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://localhost:16766", null, false);

		handshaker = wsFactory.newHandshaker(req);

		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
			String key = req.headers().get("Host");
			
			//byte o = req.content().readByte();
			
			NettyUtils.setKey(ctx, key);
		}
	}
	
	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
		// 返回应答给客户端
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static boolean isKeepAlive(FullHttpRequest req) {
		return false;
	}

}