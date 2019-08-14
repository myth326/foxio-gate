package cn.foxio.gate.tcp.service;

import org.apache.log4j.Logger;

import cn.foxio.gate.face.IChannelContainer;
import cn.foxio.gate.tcp.gateway.FoxNettyDecoder;
import cn.foxio.gate.tcp.gateway.FoxNettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * 基础tcp客户端
 * @author lucky
 *
 */
public class FoxBaseTcpService implements IChannelContainer{
	
	private Logger logger = Logger.getLogger(FoxBaseTcpService.class);


	protected EventLoopGroup workerGroup;
	protected Bootstrap bootstrap;
	protected boolean closed = false;
	protected final String host;
	protected final int port;

	protected FoxTcpServiceHandler handler;

	protected ChannelHandlerContext channel;

	public ChannelHandlerContext getChannel() {
		return channel;
	}
	@Override
	public void setChannel( ChannelHandlerContext channel ){
		this.channel = channel;
		if (channelContaier != null) {
			channelContaier.setChannel(channel);
		}
	}
	
	protected IChannelContainer channelContaier;

	public FoxBaseTcpService(String host, int port, FoxTcpServiceHandler handler , IChannelContainer channelContaier) {
		this.host = host;
		this.port = port;
		this.handler = handler;
		this.channelContaier = channelContaier;
		try {
			connect();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public boolean isConnect() {
		return channel != null && channel.channel() != null && channel.channel().isActive();
	}


	protected EventLoopGroup group = null;
	

	public void connect()  {

		group = new NioEventLoopGroup();
		handler.setClient(this);
		bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class)

				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						
						ch.pipeline().addLast("frameDecoder", new FoxNettyDecoder());  
                    	ch.pipeline().addLast("frameEncoder", new FoxNettyEncoder()); 
                    	
                    	//p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));  
                    	//p.addLast("frameEncoder", new LengthFieldPrepender(4)); 

						p.addLast("decoder", new ByteArrayDecoder());
						p.addLast("encoder", new ByteArrayEncoder());
						p.addLast(handler);
						
					}
				});

		try {
			ChannelFuture f = bootstrap.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {

		} finally {
			// 所有资源释放完成之后，清空资源，再次发起重连操作
			handler.onShutdown();
			group.shutdownGracefully();
			bootstrap.clone();
			
			logger.info(this.getClass().getName() + " socket连接中断  " + getServerInfo() );
		}
	}
	
	
	protected String getServerInfo() {
		return String.format("host=%s port=%d", host, port);
	}

}