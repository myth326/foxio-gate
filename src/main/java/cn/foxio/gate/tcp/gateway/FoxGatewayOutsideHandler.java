package cn.foxio.gate.tcp.gateway;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.tcp.data.FoxPlayerStatus;
import cn.foxio.gate.tcp.data.HeartbeatData;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.data.OriginalPackage;
import cn.foxio.gate.tools.NettyUtils;
import cn.foxio.gate.tools.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 *  外部客户端消息处理器
 * @author Lucky
 *
 */
@Component
public class FoxGatewayOutsideHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = Logger.getLogger(FoxGatewayOutsideHandler.class);

	
	
    private static  byte [] HEARTBEAT_SEQUENCE = new HeartbeatData().toByteArrays();


	private FoxGatewayAccepter accepter;

	public FoxGatewayOutsideHandler() {
	}

	public FoxGatewayOutsideHandler(FoxGatewayAccepter accepter) {
		this.accepter = accepter;
	}

	@SuppressWarnings("unused")
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		OriginalPackage omsg = (OriginalPackage) msg;
		
		InnerMessage box = new InnerMessage(String.valueOf(omsg.getCmdId() / 100), NettyUtils.getTableId(ctx), omsg.getProtoData() );
		box.setCmdId(omsg.getCmdId());
		box.setType(MessageTypeDef.ClientToService.getValue());
		box.setCustomerId(NettyUtils.getCustomerId(ctx));
		
		//不是心跳
		if (box.getCmdId() != MessageTypeDef.HeartBeat.getValue() ) {
			
			//自动补充客户端发布的消息规则
			FoxPlayerStatus cps = NettyUtils.getPlayerStatus(ctx);
			String key = NettyUtils.autoKey(ctx);
			box.setKey(key);
			box.setUserId( cps.getUserId() );
			box.setMainId( String.valueOf( box.getCmdId() / 100));
			box.setSubId(cps.getTableId());
			
			logger.info("gateway 收到client消息! "+key+" msg = " + box.getCmdId());
			
			if ( box.getCmdId() == MessageTypeDef.HeartBeat.getValue() ) {
				
				return;
			}
			
			box.setType( MessageTypeDef.ClientToService.getValue());
			accepter.acceptMsg(box, ctx);
			
			
		}else{
			//System.out.println("收到心跳并回复" + NettyUtils.getKey(ctx) +" , uid = "+ NettyUtils.getUserId(ctx)+ " time = "+ TimeUtil.getDateFormat(new Date()));
			NettyUtils.sendMsgToCtx( ctx , HEARTBEAT_SEQUENCE , MessageTypeDef.HeartBeat.getValue() );
		}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause); 
		cause.printStackTrace();
		logger.info("客户端断开连接 :  " + NettyUtils.getKey(ctx));
		long uid = NettyUtils.getUserId(ctx);
		
		System.err.println(" 异常断线 ctx = " + NettyUtils.getKey(ctx) );
		accepter.interrupt(ctx , uid );
	}
	
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		
		//子服不做心跳检测
		if (evt instanceof IdleStateEvent  && ! NettyUtils.isService(ctx) ) {
	        IdleStateEvent e = (IdleStateEvent) evt;	        
	        if ( IdleState.ALL_IDLE == e.state() && ! NettyUtils.isService(ctx) ) {
	        	long uid = NettyUtils.getUserId(ctx);
	        	//System.err.println( NettyUtils.getKey(ctx) +":: connectid = " + uid+ ":: " +e.state()  ); 
	        	//System.out.println("闲置时间过长断线 ctx = " + NettyUtils.getKey(ctx) );
	        	accepter.interrupt(ctx , uid );
	        }
	        
	    }
	}

	/**
	 * 覆盖channelActive 方法在channel被启用的时候触发（在建立连接的时候） 覆盖了 channelActive()
	 * 事件处理方法。服务端监听到客户端活动
	 * @param ctx
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("新的客户端链接 -- open");
		super.channelActive(ctx);
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = insocket.getAddress().getHostAddress();
		int port = insocket.getPort();
		String key = ip + ":" + port;
		System.out.println("create client "+key);
		NettyUtils.setKey(ctx, key);
		NettyUtils.autoRegUid(ctx);
		NettyUtils.regClient(ctx);
		
	}

	/**
	 *   覆盖了 handlerAdded() 事件处理方法。 每当从服务端收到新的客户端连接时
	 *   @param ctx
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		NettyUtils.setProto(ctx, NettyUtils.SOCKET);
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		if (insocket != null) {
//			String ip = insocket.getAddress().getHostAddress();
//			int port = insocket.getPort();
//			String key = ip + ":" + port;
		} else {
			logger.info("异常：：创建连接时， " + getClass().getSimpleName() + " ctx.channel().remoteAddress() = null ");
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		long uid = NettyUtils.getUserId(ctx);
		System.err.println("正常断线 gate client ctx = " + NettyUtils.getKey(ctx) + " time  " + TimeUtil.getDateFormat() );
		accepter.interrupt(ctx , uid );
		
	}

}