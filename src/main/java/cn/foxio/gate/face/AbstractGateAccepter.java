package cn.foxio.gate.face;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息队列接受者
 * 
 * @author lucky
 *
 */
public abstract class AbstractGateAccepter {

	protected boolean isRun = true;

	protected String tmpKey;

	public String getTmpKey() {
		return tmpKey;
	}

	public void setTmpKey(String tmpKey) {
		this.tmpKey = tmpKey;
	}

	public AbstractGateAccepter() {
	}

	/**
	 * 处理消息
	 * 
	 * @param msg
	 * @return
	 */
	protected abstract boolean handlerMsg(IMessageBox msg);

	protected ChannelHandlerContext ctx;

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * 异步接受消息
	 * 
	 * @param msg
	 * @param ctx
	 */
	public void acceptMsg(IMessageBox msg, ChannelHandlerContext ctx) {
		handlerMsg(msg);
	}

	/**
	 * 结束线程
	 */
	public void kill() {
		isRun = false;
	}

}
