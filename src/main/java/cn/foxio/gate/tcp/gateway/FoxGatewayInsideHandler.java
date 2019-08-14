package cn.foxio.gate.tcp.gateway;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.helper.FoxSubscribeHelper;
import cn.foxio.gate.tcp.data.HeartbeatData;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.data.OriginalPackage;
import cn.foxio.gate.tools.FoxProtobufUtils;
import cn.foxio.gate.tools.NettyUtils;
import cn.foxio.gate.tools.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 网关-子服内部消息处理器
 * 
 * @author Lucky
 *
 */
@Component
public class FoxGatewayInsideHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = Logger.getLogger(FoxGatewayInsideHandler.class);



	private static byte[] HEARTBEAT_SEQUENCE = FoxProtobufUtils
			.encode(new InnerMessage(GatewayDef.GATE_CENTER, GatewayDef.SUBSCRIPTION_ID_DEFAULT,
					new HeartbeatData().toByteArrays()).setType(MessageTypeDef.HeartBeat.getValue()));

	private FoxGatewayAccepter accepter;

	public FoxGatewayInsideHandler() {
	}

	public FoxGatewayInsideHandler(FoxGatewayAccepter accepter) {
		this.accepter = accepter;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		OriginalPackage omsg = (OriginalPackage) msg;
		InnerMessage box = FoxProtobufUtils.decode(InnerMessage.class, omsg.getProtoData());
		if (box == null) {
			// 错误的消息!
			String v = "收到一个非法消息 ip:port" + NettyUtils.getKey(ctx) + " , msg byte array = " + omsg.getProtoData();
			logger.error(v);
			return;
		}

		// 消息为不空, 且消息不是心跳
		if (box != null) {
			if (box.getType() != MessageTypeDef.HeartBeat.getValue()) {
				String key = NettyUtils.autoKey(ctx);
				//logger.info("gateway 收到消息! " + key + " msg = " + box);
				box.setKey(key);
				accepter.acceptMsg(box, ctx);
			} else {
				// System.out.println("收到心跳并回复" + NettyUtils.getKey(ctx) +" , uid = "+
				// NettyUtils.getUserId(ctx)+ " time = "+ TimeUtil.getDateFormat(new Date()));
				NettyUtils.sendMsgToCtx(ctx, HEARTBEAT_SEQUENCE, MessageTypeDef.HeartBeat.getValue());
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		long uid = NettyUtils.getUserId(ctx);
		//System.err.println(" 异常断线 ctx = " + NettyUtils.getKey(ctx));
		accepter.interrupt(ctx, uid);
		String key = NettyUtils.getKey(ctx);
		logger.info("异常子服断线:: key= "+key );
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);

		// 子服不做心跳检测
		if (evt instanceof IdleStateEvent && !NettyUtils.isService(ctx)) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (IdleState.ALL_IDLE == e.state() && !NettyUtils.isService(ctx)) {
				long uid = NettyUtils.getUserId(ctx);

				System.err.println(NettyUtils.getKey(ctx) + ":: connectid = " + uid + ":: " + e.state());
				System.out.println("闲置时间过长断线 ctx = " + NettyUtils.getKey(ctx));
				accepter.interrupt(ctx, uid);
				logger.info("子服闲置时间过长断线 ctx = " + NettyUtils.getKey(ctx) );
			}

		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = insocket.getAddress().getHostAddress();
		int port = insocket.getPort();
		String key = ip + ":" + port;
		System.out.println("新的子服链接 -- " + key);

		NettyUtils.setKey(ctx, key);
		NettyUtils.autoRegUid(ctx);
		NettyUtils.regServer(ctx);
		
		logger.info("新的子服链接 -- "+key );
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		NettyUtils.setProto(ctx, NettyUtils.SOCKET);
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		if (insocket != null) {
			String ip = insocket.getAddress().getHostAddress();
			int port = insocket.getPort();
			String key = ip + ":" + port;
			logger.info(getClass().getSimpleName() + "::" + "创建连接" + key);
		} else {
			System.err.println("创建连接但， " + getClass().getSimpleName() + " ctx.channel().remoteAddress() = null ");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		long uid = NettyUtils.getUserId(ctx);
		System.err.println("正常断线 ctx = " + NettyUtils.getKey(ctx) + " time  " + TimeUtil.getDateFormat());
		accepter.interrupt(ctx, uid);
		
		String key = NettyUtils.getKey(ctx);
		logger.info("子服断线:: key= "+key );
		FoxSubscribeHelper.getIstance().removeSubscribe(key);
	}

}