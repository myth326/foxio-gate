package cn.foxio.gate.tools;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cn.foxio.gate.tcp.gateway.FoxNettyDecoder;
import cn.foxio.gate.tcp.gateway.FoxNettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * 工具
 * @author lucky
 *
 */
public class ServerUtils {

	
	 /*** 
     *  检测对应host/port是否有 socket servers 存在 [用客户端能连上]
     * @param host 
     * @param port 
     * @throws UnknownHostException  
     */  
    @SuppressWarnings("resource")
	public static boolean isPortUsing(String host,int port) {  
        boolean flag = false;  
        try {  
        	InetAddress address = InetAddress.getByName(host);  
            new Socket(address,port);  
            flag = true;  
        } catch (IOException e) {  
              
        }  
        return flag;  
    }
    
    public static void main(String[] args) {
		
    	boolean b = isPortUsing("127.0.0.1", 8000);
    	System.out.println( b);
    	
	}
    
	/**
	 * 检测对应host/port是否有 socket servers 存在 [用客户端能连上]
	 * @param host
	 * @param port
	 * @return
	 */
	static public boolean checkHasSocketServer(String host, int port){
		
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class);
		b.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				//pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				//pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				ch.pipeline().addLast("frameDecoder", new FoxNettyDecoder());  
            	ch.pipeline().addLast("frameEncoder", new FoxNettyEncoder()); 
				
				pipeline.addLast("decoder", new ByteArrayDecoder());
				pipeline.addLast("encoder", new ByteArrayEncoder());
				//pipeline.addLast("handler", new TcpClientHandler());
			}
		});
		b.option(ChannelOption.SO_KEEPALIVE, true);

		try {
			b.connect(host, port).sync().channel();
		} catch (Exception e) {
			return false;
		}
		group.shutdownGracefully();
		return true;
	}
	
	
	/**
	 * 取本机IP
	 * @return
	 */
	static public String getLocalIp(){
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
				
	}
	
	/**
     * IceWee 2013.07.19
     * 获取本地IP列表（针对多网卡情况）
     *
     * @return
     */
    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }
	
	
	
	
	
	
}
