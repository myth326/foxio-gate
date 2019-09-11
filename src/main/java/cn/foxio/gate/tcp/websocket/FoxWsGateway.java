package cn.foxio.gate.tcp.websocket;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cn.foxio.gate.tcp.gateway.FoxGatewayAccepter;
import cn.foxio.gate.tools.ServerUtils;
import cn.foxio.gate.tools.TimeUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 创建一个网关消息队列
 * 
 * @author admin
 *
 */
public class FoxWsGateway {

	private Logger logger = Logger.getLogger(FoxWsGateway.class);

	private int port;

	/** 用于分配处理业务线程的线程组个数 */
	protected int bizGroupSize = Runtime.getRuntime().availableProcessors() * 2;

	/** 业务出现线程大小 */
	protected int bizThreadSize = 4;

	private EventLoopGroup bossGroup = new NioEventLoopGroup(bizGroupSize);

	private EventLoopGroup workerGroup = new NioEventLoopGroup(bizThreadSize);

	/**
	 * 创建一个网关消息队列
	 * 
	 * @param ip
	 * @param port
	 */
	public FoxWsGateway(int port) {
		this.port = port;

	}

	public void start() throws Exception {

		FoxGatewayAccepter accepter = new FoxGatewayAccepter();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).childOption(ChannelOption.SO_REUSEADDR, true);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				
				// 读超时
				private static final int READ_IDEL_TIME_OUT = 30;
				// 写超时
				private static final int WRITE_IDEL_TIME_OUT = 30;
				// 写读超时
				private static final int ALL_IDEL_TIME_OUT = 60;

				@Override
				public void initChannel(SocketChannel ch) throws Exception {

					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new IdleStateHandler(READ_IDEL_TIME_OUT, WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT,
							TimeUnit.SECONDS));

					// pipeline.addLast(new AcceptorIdleStateTrigger());
					//pipeline.addLast(new LoggingHandler(LogLevel.ERROR));

					pipeline.addLast("http-codec", new HttpServerCodec());
					pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
					pipeline.addLast("http-chunked", new ChunkedWriteHandler());
					pipeline.addLast("handler", new FoxWsGatewayServerHandler(accepter));
				}
			});
			
			b.bind( port).sync();
			logger.info("websocket 服务器已启动 port = " + port );
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(String.format("Create WsService ip = %s , port = %s , at time = %s !", "....", port,
				TimeUtil.getDateFormat()));

	}

	public void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

}
