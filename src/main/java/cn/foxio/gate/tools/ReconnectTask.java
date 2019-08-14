package cn.foxio.gate.tools;

import java.util.TimerTask;

import cn.foxio.gate.face.ICallBack;

/**
 * 断线重连定时器 (仅限阻塞式服务端使用)
 * 
 * @author BeiTown * 用法示例： ReconnectTask(new ICallBack() { public void execute()
 *         {
 * 
 *         //此处添加测试代码
 * 
 *         }});
 * 
 */
public class ReconnectTask extends TimerTask {
	/**
	 * 回调
	 */
	private ICallBack callBack;

	/**
	 * 构造函数定义回调方法
	 * 
	 * @param callBack
	 */
	public ReconnectTask(ICallBack callBack) {
		this.callBack = callBack;
	}

	@Override
	public void run() {

		callBack.execute();

	}

}
