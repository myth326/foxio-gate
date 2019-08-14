package cn.foxio.gate.face;

import cn.foxio.gate.face.IObject;

/**
 * 命令接口
 * @author BeiTown
 *
 */
public interface IPlayerCommand<M, T> extends IObject {
	
	
	
	/**
	 * 绑定数据 
	 * @param box
	 * @param msg
	 * @param args
	 * @return
	 */
	public boolean binding(M box, T msg , Object ...args);
	
	/**
	 * 检测 合法性较验
	 * @param box
	 * @param msg
	 * @param args
	 * @return
	 */
	public boolean check(M box, T msg , Object ...args);

	/**
	 * 执行命令
	 * @param box 
	 * @param msg
	 * @param args
	 * @return
	 */
	public Object execute(M box, T msg , Object ...args);
	
}
