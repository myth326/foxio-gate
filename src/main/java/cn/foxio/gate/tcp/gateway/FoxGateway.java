package cn.foxio.gate.tcp.gateway;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

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


	private int port1;
	private int port2;

	/** 用于分配处理业务线程的线程组个数 */
	protected int bizGroupSize = Runtime.getRuntime().availableProcessors() * 2; 

	/** 业务出现线程大小 */
	protected int bizThreadSize = 4;

	private EventLoopGroup bossGroup = new NioEventLoopGroup(bizGroupSize);

	private EventLoopGroup workerGroup = new NioEventLoopGroup(bizThreadSize);
	
	
	public FoxGateway(){}
	

	/**
	 * 创建一个网关消息队列
	 * @param ip
	 * @param port
	 */
	public FoxGateway(int port1, int port2 ) {
		this.port1 = port1;
		this.port2 = port2;
		//System.out.println(String.format("Create Gateway ip = {%s} socket client {%s};  inner server {%s}", "", port1 , port2));
	}
	

	


	public void start() throws Exception {
		
		final FoxGatewayAccepter accepter = new FoxGatewayAccepter();
		
		 EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup,workerGroup)
	            		.channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port1))
	            		//.channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port2))
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
	                            
	                            if(ch.localAddress().getPort() == port1){
	                            	FoxGatewayOutsideHandler hdl = new FoxGatewayOutsideHandler( accepter );
	                                ch.pipeline().addLast(hdl);
	                            }else{
	                                ch.pipeline().addLast(new FoxGatewayInsideHandler( accepter ));
	                            }

	                        };
	                        
	                    });
	             // 绑定端口，开始接收进来的连接
	             ChannelFuture future1 = sbs.bind(port1).sync();  
	             ChannelFuture future2 = sbs.bind(port2).sync();
	             
	             System.out.println("Server start listen at port1 = " + port1 + ",port2 = " + port2 );
	             
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

	public int getPort1() {
		return port1;
	}
	public int getPort2() {
		return port2;
	}


	public void setPort1(int port1) {
		this.port1 = port1;
	}


	public void setPort2(int port2) {
		this.port2 = port2;
	}
	

}
