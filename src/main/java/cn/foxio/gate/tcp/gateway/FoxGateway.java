package cn.foxio.gate.tcp.gateway;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import cn.foxio.gate.tools.ServerUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * 创建一个网关消息队列
 * @author lucky
 *
 */
@Component
public class FoxGateway {

	
	private Logger logger = Logger.getLogger(FoxGateway.class);

	private String host;
	private int insidePort;
	private int outsidePort;

	/** 用于分配处理业务线程的线程组个数 */
	protected int bizGroupSize = Runtime.getRuntime().availableProcessors(); 

	/** 业务出现线程大小 */
	protected int bizThreadSize = 4;

	private EventLoopGroup bossGroup = new NioEventLoopGroup(bizGroupSize);

	private EventLoopGroup workerGroup = new NioEventLoopGroup(bizThreadSize);
	
	
	public FoxGateway(){}
	

	/**
	 * 创建一个网关消息队列, outsidePort
	 * @param ip
	 * @param port
	 */
	public FoxGateway(int insidePort, int outsidePort ) {
		this.host = ServerUtils.getLocalIp();
		this.insidePort = insidePort;
		this.outsidePort = outsidePort;
		//System.out.println(String.format("Create Gateway ip = {%s} socket client {%s};  inner server {%s}", "", insidePort , outsidePort));
	}
	

	


	public void start() throws Exception {
		
		final FoxGatewayAccepter accepter = new FoxGatewayAccepter();
		
		 EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup,workerGroup)
	            		.channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(insidePort))
	            		//.channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(outsidePort))
	                    .childHandler(new ChannelInitializer<SocketChannel>() {
	                    	// 读超时
	                        private static final int READ_IDEL_TIME_OUT = 30; 
	                     // 写超时
	                        private static final int WRITE_IDEL_TIME_OUT = 30;
	                     // 读写超时
	                        private static final int ALL_IDEL_TIME_OUT = 70;
	                        @Override
	                        protected void initChannel(SocketChannel ch) throws Exception {
	                            ch.pipeline().addLast(new IdleStateHandler(READ_IDEL_TIME_OUT, WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT, TimeUnit.SECONDS));
	                            //ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));//日志
	                        	// tmp  new LengthFieldBasedFrameDecoder(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast)
	                        	ch.pipeline().addLast("frameDecoder", new FoxNettyDecoder());  
	                        	ch.pipeline().addLast("frameEncoder", new FoxNettyEncoder()); 
	                            
	                            if(ch.localAddress().getPort() == outsidePort){
	                            	FoxGatewayOutsideHandler hdl = new FoxGatewayOutsideHandler( accepter , host + ":" + outsidePort );
	                                ch.pipeline().addLast(hdl);
	                            }else{
	                                ch.pipeline().addLast(new FoxGatewayInsideHandler( accepter , host + ":" + insidePort));
	                                
	                            }

	                        };
	                        
	                    });
	             // 绑定端口，开始接收进来的连接
	             //ChannelFuture future1 = sbs.bind(insidePort).sync();  
	             //ChannelFuture future2 = sbs.bind(outsidePort).sync();
	             sbs.bind(insidePort).sync();  
	             sbs.bind(outsidePort).sync();
	             
	             System.out.println("Server start listen at insidePort = " + insidePort + ",outsidePort = " + outsidePort );
	             
	             logger.info(" 服务器已启动");
	             //future.channel().closeFuture().sync();
	        } catch (Exception e) {
	           // bossGroup.shutdownGracefully();
	            //workerGroup.shutdownGracefully();
	            e.printStackTrace();
	            System.err.println("网关启动失败！");
	        }
	}

	public void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	public int getInsidePort() {
		return insidePort;
	}
	public int getOutsidePort() {
		return outsidePort;
	}


//	public void setInsidePort(int insidePort) {
//		this.insidePort = insidePort;
//	}
//
//
//	public void setOutsidePort(int outsidePort) {
//		this.outsidePort = outsidePort;
//	}
	

}
