package cn.foxio.gate.tcp.service;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;

import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.face.ICallBack;
import cn.foxio.gate.face.IChannelContainer;
import cn.foxio.gate.face.IFoxAccepter;
import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.tcp.SubServiceBase;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.gateway.FoxGatewayOutsideHandler;
import cn.foxio.gate.tools.FoxProtobufUtils;
import cn.foxio.gate.tools.NettyUtils;
import cn.foxio.gate.tools.ReconnectTask;
import cn.foxio.gate.tools.TimeTaskManager;
import cn.foxio.gate.tools.TimeUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 * TCP服务器
 * @author lucky
 *
 */
public class FoxTcpService implements IChannelContainer {

	
	private Logger logger = Logger.getLogger(FoxGatewayOutsideHandler.class);

	
	/**
	 * 消息队列，如连接通道中断，则将消息暂时放到该队列中
	 */
	protected ConcurrentLinkedQueue<IMessageBox> msgQueue = new ConcurrentLinkedQueue<IMessageBox>();

	protected String host;
	protected int port;

	protected ScheduledExecutorService actionService;
	
	protected IFoxAccepter<InnerMessage> accepter;
	
	private SubServiceBase box;

	public String getKey() {
		return host + ":" + port;
	}

	public FoxTcpService(String host, int port, IFoxAccepter<InnerMessage> accepter , SubServiceBase box) {

		this.host = host;
		this.port = port;
		this.accepter = accepter;
		this.box = box;
		
		createServer(null);
	}
	
	private JobDataMap jobDataMap;
	
	private void addTimeTask() {
		
		
		int dealy = 2 * 1000;
		
		TimeTaskManager.getInstance().removeTask(jobDataMap);

		jobDataMap = TimeTaskManager.getInstance().addTask(obj -> {
			createServer(obj);
		}, new Object(), dealy );
		
		
	}
	
	protected FoxBaseTcpService client;
	
	private void createServer( Object obj ) {
		createServer();
	}
	
	

	/**
	 * 创建服务器，有重连功能
	*/	 
	protected void createServer() {
		IChannelContainer container = this;
		
		
		
		if (actionService != null) {
			actionService.shutdown();
			actionService = null;
		}
		
		
		actionService = Executors.newSingleThreadScheduledExecutor();
		
		actionService.schedule(new ReconnectTask(new ICallBack() {

			protected FoxBaseTcpService client;

			// 开启断线重连定时器
			@Override
			public void execute() {
				try {

					String message = "gateway connting [host=%s,port=%s], at time "
							+ TimeUtil.getDateFormat(new Date());

					message = String.format(message, host, port);
					logger.info(message);

					FoxTcpServiceHandler hdl = new FoxTcpServiceHandler(accepter);
					client = new FoxBaseTcpService(host, port, hdl, container);

				} catch (Exception e) {
					System.out.println("无法连接到服务器，2秒后重新连接...");
					return;
				}
			}

		}),2 , TimeUnit.SECONDS );
		
		
		
	}

	//protected ScheduledExecutorService actionService;
	
	
	protected ChannelHandlerContext channel = null;

	protected void handshake() {
		//将原有的订阅消息，重新订阅；
		for ( InnerMessage box : box.getSubScribeLst()){
			sendStringMsg(box);
		}
		// 将积压的数据重新发送
		sendStringMsg(null);
	}
	
	@Override
	public void setChannel(ChannelHandlerContext channel) {
		this.channel = channel;

		logger.info("name = " + this.accepter.getServerName() + " setChannel=" + channel);
		if (channel != null) {
			// String s = String.format(" service tcp connected ! host= %s , port = %s this
			// = " + this, host, port);
			// System.out.println(s);
			handshake();
			//sendStringMsg(null);
			
		}else {
			logger.info("连接断线，启动定时任务，2秒后重连!");
			addTimeTask();
		}
	}

	public ChannelHandlerContext getChannel() {
		return channel;
	}

	/**
	 * 发送消息， 如连接未开启，存入队列，当连接成功后，重新发送;
	 * 
	 * @param box
	 */
	public boolean sendStringMsg(IMessageBox box) {

		// System.err.println( "sendStringMsg , this = " +this + ", box=" + box
		// );
		bindMainId(box);
		try {

			if (getChannel() == null) {
				if (box != null) {
					msgQueue.offer(box);
					logger.error("当前通道中断，消息存入队列中，box=" + box.toJson() + "!");
				}
				return false;
			}

			byte[] ba = null;

			// 将未成功发送的消息取出，按照顺序重新发送!
			while (!msgQueue.isEmpty()) {

				IMessageBox item = msgQueue.poll();
				//logger.info("name = " + this.accepter.getServerName() + " to gateway box = " + item.toJson() + this);
				
				System.out.println( item.toJson() );
				ba = FoxProtobufUtils.encode(item);
				System.out.println( Arrays.toString(ba));
				sendStringMsgByteArrays(ba, item);

			}
			if (box != null) {
				ba = FoxProtobufUtils.encode(box);
				sendStringMsgByteArrays(ba, box);
				//logger.info("name = " + this.accepter.getServerName() + " to gateway msg = " + box.toJson());
			}

		} catch (Exception e) {

			System.out.println("消息发送失败, Exception info = " + e.toString());
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
	protected void sendStringMsgByteArrays(byte[] data, IMessageBox item) {

		if (getChannel() != null) {
			// if (data.length > Short.MAX_VALUE / 2) {
			// logger.info("消息过长，发送失败!");
			// return;
			// }

			if (data.length >= Integer.MAX_VALUE) {
				logger.error("消息長度太長");
			}

			NettyUtils.sendMsgToCtx(getChannel(), data, item.getCmdId() );

//			String v = " send msg to [host=%s,port=%s], at time " + TimeUtil.getDateFormat(new Date())
//					+ " . \t\n 	msg = %s";
//			v = String.format(v, host, port, item.toJson());
			// logger.info(v);
			//System.out.println( v );

		} else {
			logger.warn("消息发送失败,连接尚未建立! MQTcpService ");
		}

	}
	
	/**
	 * 绑定主命令为模块id
	 * @param msg
	 */
	protected void bindMainId(IMessageBox msg) {
		
		if ( msg != null && msg.getType() == MessageTypeDef.SendToClient.getValue() ) {
			if ( msg.getMainId()== null || msg.getMainId().length() == 0 ) {
				msg.setMainId(String.valueOf(  msg.getCmdId() / 100 ));
			}
		}
	}
	
}