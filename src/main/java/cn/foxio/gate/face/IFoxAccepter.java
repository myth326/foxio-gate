package cn.foxio.gate.face;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息适配器
 * @author Lucky
 *
 */
public interface IFoxAccepter<T> {

	

	/**
	 * 异步接受消息
	 * 
	 * @param msg
	 * @param ctx
	 */
	void acceptMsg(T msg, ChannelHandlerContext ctx);
	
	/**
	 * 取得服务器名字
	 * @return
	 */
	String getServerName();
	
	/**
	 * 通讯信道
	 * @return
	 */
	ChannelHandlerContext getCtx();
	/**
	 * 通讯信道
	 * @param ctx
	 */
	void setCtx(ChannelHandlerContext ctx);
	

	/**
	 * 杀死
	 */
	void kill();
	
}
