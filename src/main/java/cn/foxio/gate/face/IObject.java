package cn.foxio.gate.face;

import cn.foxio.gate.tools.GsonUtil;

/**
 * 基础对象
 * @author lucky
 *
 */
public interface IObject {

	/**
	 * 转JSON
	 * @return
	 */
	public default String toJson() {
		return GsonUtil.toJson(this);
	};
	
	
}
