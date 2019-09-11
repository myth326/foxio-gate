package cn.foxio.gate.tcp.client;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import cn.foxio.gate.face.IChannelContainer;
import cn.foxio.gate.face.IFoxAccepter;
import cn.foxio.gate.face.IMsg;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tools.NettyUtils;
import cn.foxio.gate.tools.TimeUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * TCP客户端，不带重连机制，用于测试客户端
 * @author lucky
 *
 */
public class FoxTcpClient implements IChannelContainer {

	private Logger logger = Logger.getLogger(FoxTcpClient.class);
	protected boolean isRun = true;

	/**
	 * 消息队列，如连接通道中断，则将消息暂时放到该队列中
	 */
	protected ConcurrentLinkedQueue<IMsg> msgQueue = new ConcurrentLinkedQueue<IMsg>();

	protected String host;
	protected int port;

	//protected String name;
	
	
	
	private BaseTcpClient client;
	
	private IFoxAccepter<InnerMessage> accepter = null;

	public String getKey() {
		return host + ":" + port;
	}

	public FoxTcpClient(String host, int port, IFoxAccepter<InnerMessage> accepter) {

		this.host = host;
		this.port = port;
		this.accepter = accepter;
		createClient();
		//this.start();
	}

	/**
	 * 创建客户端【无重连功能】
	 */
	protected void createClient() {

		IChannelContainer container = this;

		String message = "connting to [host=%s,port=%s], at time " + TimeUtil.getDateFormat(new Date());

		message = String.format(message, host, port);
		logger.info(message);
		FoxTcpClientHandler hdl = new FoxTcpClientHandler(accepter , host+":"+port);
		client = new BaseTcpClient(host, port, hdl, container);

	}


	protected ChannelHandlerContext channel = null;

	protected void handshake() {
		// 发送握手，绑定key
		//LHandshakeMQData hd = new LHandshakeMQData(0);

		//logger.info("name = " + this.name + "发送握手:" + hd.toJson());
		// 握手需要第一个发送
		//sendStringMsgByteArrays( hd);

		// 将积压的数据重新发送
		while( msgQueue.size() > 0 ) {
			IMsg msg = msgQueue.poll();
			sendStringMsgByteArrays(msg);
		}
		
	}
	
	
	@Override
	public void setChannel(ChannelHandlerContext channel) {
		this.channel = channel;

		//logger.info("name = " + this.name + " setChannel=" + channel);
		if (channel != null) {
			handshake();
		}else{
			System.out.println("channel == null");
			kill();
		}
	}

	public ChannelHandlerContext getChannel() {
		return channel;
	}

	/**
	 * 发送消息， 如连接未开启，存入队列，当连接成功后，重新发送;
	 * 
	 * @param msg
	 */
	public boolean sendStringMsg(IMsg msg) {
		try {
			if (msg != null) {
				
				
				if ( this.getChannel() == null ) {
					msgQueue.offer(msg);
				}else {
					sendStringMsgByteArrays(msg);
				}
				
			}
		} catch (Exception e) {
			logger.warn("消息发送失败, Exception info = " + e.toString());
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 发送消息， 如连接未开启，存入队列，当连接成功后，重新发送;
	 * 
	 * @param msg
	 */
	public void sendStringMsgByteArrays(IMsg item) {

		if (getChannel() != null && item != null ) {
			byte[] data = item.toByteArrays();
			if (data.length > Short.MAX_VALUE) {
				logger.info("消息过长，发送失败!");
				return;
			}

			try {

				// byte [] ba = new byte[ 2 + data.length ];
				
				NettyUtils.sendMsgToCtx(getChannel(), data , item.getCmdId() );
				
				if ( item != null) {
					String v = " send msg to [host=%s,port=%s], at time " + TimeUtil.getDateFormat(new Date())
							+ " . \t\n 	msg = %s";
					v = String.format(v, host, port, item.toJson());
					logger.debug(v);
				}
				//System.out.println( v );

			} catch (Exception e) {

				// String ecls = e.getClass().getName();//ClosedChannelException
				// System.out.println( "Exception:"+ecls );
				//
				// if ("java.nio.channels.ClosedChannelException".equals(ecls)){
				// //this.doConnect();
				// }else if
				// ("java.nio.channels.NotYetConnectedException".equals(ecls)){
				// //this.doConnect();
				// }else if
				// ("java.nio.channels.ClosedChannelException".equals(ecls)){
				// //this.doConnect();
				// }

				logger.error(e);
			}

		} else {
			logger.warn("消息发送失败,连接尚未建立!TcpClient");
		}

	}



	

	/**
	 * 结束线程
	 */
	public void kill() {
		
		isRun = false;
		if ( client != null ) {
			client.shutdown();
		}
		
	}
}