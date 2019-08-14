package cn.foxio.gate.face;


/**
 * 与客户端通讯的消息
 * @author Lucky
 *
 */
public interface IMsg extends IObject{

	/**
	 * 取得模块id
	 * @return
	 */
	int getModuleId();
	/**
	 * 指令号
	 * @return
	 */
	int getCmdId();
	/**
	 * 指令描述
	 * @return
	 */
	String getDesc();
	/**
	 * 转二进制
	 * @return
	 */
	byte[] toByteArrays();
	
	/**
	 * 处理该指令的全类名
	 * @return
	 */
	String getCmdClassName();
	
}
