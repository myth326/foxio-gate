package cn.foxio.gate.face;

import cn.foxio.gate.tools.FoxProtobufUtils;

/**
 *  内部消息
 * @author Lucky
 *
 */
public interface IInnerMessage extends IObject {
	
	/**
	 * 通讯指令
	 * @return
	 */
	public int getCmdId();
	
	/**
	 * 二进制序列化
	 * @return
	 */
	default byte[] toByteArrays() {
		return FoxProtobufUtils.encode(this);
	}
	
}
