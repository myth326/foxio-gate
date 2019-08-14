package cn.foxio.gate.tcp.client;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.face.IChannelContainer;
import cn.foxio.gate.face.IFoxAccepter;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.data.OriginalPackage;
import cn.foxio.gate.tools.NettyUtils;
import cn.foxio.gate.tools.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 子服socket处理器
 * 
 * @author Lucky
 *
 */
public class FoxTcpClientHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = Logger.getLogger(FoxTcpClientHandler.class);


	private boolean isConnected;

	private IFoxAccepter<InnerMessage> accepter;

	private IChannelContainer client;

	public IChannelContainer getClient() {
		return client;
	}

	public void setClient(IChannelContainer client) {
		this.client = client;
	}

	public FoxTcpClientHandler(IFoxAccepter<InnerMessage> accepter) {
		this.accepter = accepter;
	}

	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		//String key = getKey(ctx);
		OriginalPackage omsg = (OriginalPackage) msg;
		InnerMessage box = new InnerMessage(String.valueOf(omsg.getCmdId() / 100), NettyUtils.getTableId(ctx),
				omsg.getProtoData());
		box.setCmdId(omsg.getCmdId());
		box.setCustomerId(NettyUtils.getCustomerId(ctx));

		if (box.getCmdId() == MessageTypeDef.HeartBeat.getValue()) {
			return;
		}
		accepter.acceptMsg(box, ctx);
	}

	/**
	 * 取得由IP/PORT组成的KEY;
	 * 
	 * @param ctx
	 * @return
	 */
	protected String getKey(ChannelHandlerContext ctx) {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = insocket.getAddress().getHostAddress();
		int port = insocket.getPort();
		String key = ip + ":" + port;
		return key;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// System.out.println("与网关连接成功 ：" + TimeUtil.getDateFormat() );
		ctx.fireChannelActive();
		client.setChannel(ctx);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		accepter.setCtx(ctx);
		this.isConnected = true;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.close();
		String key = getKey(ctx);
		logger.error("exceptionCaught  key = " + key + " cause = " + cause);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		ctx.close();
		System.out.println("handlerRemoved connect break !");
		client.setChannel(null);
	}

	/**
	 * 关闭连接
	 */
	public void onShutdown() {
		logger.info("TcpClientHandler onShutdown !" + TimeUtil.getDateFormat());
	}

}
