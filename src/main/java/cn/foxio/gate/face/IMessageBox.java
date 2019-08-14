package cn.foxio.gate.face;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * 消息接口
 * @author lucky
 *
 */
public interface IMessageBox extends IInnerMessage {

	/**
	 * 类型   TypeLMessage
	 * @return 
	 */
	public int getType();
	
	/**
	 * 类型   TypeLMessage
	 * @param type
	 * @return
	 */
	public IMessageBox setType(int type);

	/**
	 * 主要标识
	 * @return
	 */
	public String getMainId();
	
	/**
	 * 主标识
	 * @param v
	 */
	public void setMainId(String v);
	
	
	/**
	 * 次要标识
	 * @return
	 */
	public String getSubId();
	/**
	 * 次标识
	 * @param v
	 */
	public void setSubId(String v);
	
	/**
	 * 用户id 针对用户发送消息的标识
	 * @return
	 */
	public long getUserId();
	/**
	 * 用户ID
	 * @param uid
	 */
	public void setUserId(long uid);

	/**
	 * key ip:port
	 * @return
	 */
	public String getKey();
	/**
	 * key ip:port
	 * @param key
	 */
	
	public void setKey(String key);
	
	/**
	 * 消息数据二进制数组 【该数据用protobuf编码解码】
	 * @return
	 */
	@JSONField(serialize = false)
	public byte[] getDataContent();
	/**
	 * 消息体
	 * @return
	 */
	public Object getMsgData();
	/**
	 * 消息体
	 * @param msgData
	 */
	public void setMsgData(Object msgData );

		
	/**
	 * 转成json 字符串
	 * @return
	 */
	public String toJson();
	
	/**
	 * cmd id
	 * @return
	 */
	public int getCmdId();
	
	/**
	 * 商户ID
	 * @return
	 */
	public int getCustomerId();
	
}
