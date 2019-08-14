package cn.foxio.gate.face;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息队列接受者
 * @author lucky
 *
 * @param <T>
 */
public abstract class AbstractAccepter<T> extends Thread  implements IFoxAccepter<T> {

	protected boolean isRun = true;
	

	/**
	 * 消息列表
	 */
	protected ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<T>();

	/**
	 * 日志
	 * @return
	 */
	protected abstract Logger getLogger();


	public AbstractAccepter(String threadName) {
		super(threadName);
	}

	public AbstractAccepter() {
	}

	@Override
	public void run() {

		super.run();
		
		dorun();

	}
	
	/**备用方法**/
	protected void loop() {}
	
	
	protected void dorun()
	{
		int num = 0;
		while (isRun) {
			try {
				
				while (!queue.isEmpty()) {
					try {
						T obj = queue.poll();
						handlerMsg(obj);
						loop();
						num++;
					} catch (Exception e) {

						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						String errInfo = sw.toString();
						getLogger().info(errInfo);
						System.out.println(errInfo);
					}
					// 处理5条数据，无论是否还有消息未处理完，休眠1毫秒,将CPU让给其它线程
					if (num > 10) {
						sleep(1);
						num = 0;
					}
				}
				loop();
				sleep(1);
			} catch (Exception e) {
				e.printStackTrace();

				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String errInfo = sw.toString();
				getLogger().info(errInfo);
			}
		}
	}
	
	
	/**
	 * 处理消息
	 * @param msg
	 * @return
	 */
	protected abstract boolean handlerMsg(T msg);

	protected ChannelHandlerContext ctx;
	@Override
	public ChannelHandlerContext getCtx() {
		return ctx;
	}
	@Override
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * 异步接受消息
	 * 
	 * @param msg
	 * @param ctx
	 */
	@Override
	public void acceptMsg(T msg, ChannelHandlerContext ctx) {
		queue.offer(msg);
		if (ctx == null ) {
			this.ctx = ctx;
		}
	}

	

	/**
	 * 结束线程
	 */
	@Override
	public void kill() {
		//isRun = false;
	}
	@Override
	public String getServerName() {
		return "abstract ServerName ";
	}

}
