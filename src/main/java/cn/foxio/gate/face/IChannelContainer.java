package cn.foxio.gate.face;

import io.netty.channel.ChannelHandlerContext;

/**
 * socket通道容器
 * @author lucky
 *
 */
public interface IChannelContainer {

	/**
	 * 设置通讯通道
	 * @param channel
	 */
	public void setChannel( ChannelHandlerContext channel );
}
