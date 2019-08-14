package cn.foxio.gate.def;

/**
 * 网关发布订阅  key 定义
 * @author Lucky
 *
 */
public class GatewayDef {

	
	
	/**
	 * gate调度中心
	 */
	static public final String GATE_CENTER = "gateCenter";
	
	/**
	 * 所有的子标识 ，即不区分子标识
	 */
	static public final String SUBSCRIPTION_ID_DEFAULT = "alls";
	
	/**
	 * 客户端id变更
	 */
	static public final String USER_ID_CHANGE = "UserIdChange";
	
	/**
	 * 房间id变更
	 */
	static public final String TABLE_ID_CHANGE = "TableIdChange";	
	
	/**
	 * 客户端下线
	 */
	static public final String CLIENT_OFF_LINE = "ClientOffLine";
	
	/**
	 * 重复登陆
	 */
	static public final String REPEAT_LOGIN = "RepeatLogin";
	
	
}
