package cn.foxio.gate.tcp.data;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.tools.GsonUtil;

/**
 * 订阅数据
 * @author lucky
 *
 */
public class SubscribeData {

	
	private String key;
	/**
	 * 创建的时间
	 */
	private Date createDate;
	
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	private ConcurrentHashMap<String, Object> msgMap = new ConcurrentHashMap<>();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ConcurrentHashMap<String, Object> getMsgMap() {
		return msgMap;
	}

	public void setMsgMap(ConcurrentHashMap<String, Object> msgMap) {
		this.msgMap = msgMap;
	}
	
	
	/**
	 * 检测消息是否有订阅
	 * @param main
	 * @param sub
	 * @return
	 */
	public boolean isSubscribe( String main , String sub )
	{
		if ( main == null){
			return false;
		}
		Object m = msgMap.get(main);
		
		if ( m != null ){
			String allDef = "0";
			if ( sub == null || GatewayDef.SUBSCRIPTION_ID_DEFAULT.equals(sub) || sub=="" || sub.equals(allDef) ){
				return true;
			}else{
				Object s = msgMap.get(main+"."+sub);
				if ( s != null ) {
					return true;
				}
			} 
		}
		return false;
	}
	
	
	/**
	 * 检测消息是否有订阅 
	 * @param main
	 * @param sub
	 * @param gameId
	 * @return
	 */
	public boolean isSubscribe( String main , String sub , String gameId)
	{
		if ( main == null || gameId == null){
			return false;
		}
		String mainKey = main+"."+gameId;
		Object m = msgMap.get(mainKey);
		
		if ( m != null ){
			String allDef = "0";
			if ( sub == null || GatewayDef.SUBSCRIPTION_ID_DEFAULT.equals(sub) || sub=="" || sub.equals(allDef) ){
				return true;
			}else{
				Object s = msgMap.get(mainKey+"."+sub);
				if ( s != null ) {
					return true;
				}
			} 
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		return GsonUtil.toJson(this);
	}
	
}
