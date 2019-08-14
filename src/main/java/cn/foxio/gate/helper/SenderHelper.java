package cn.foxio.gate.helper;

import java.util.concurrent.ConcurrentHashMap;

import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.face.IMsg;
import cn.foxio.gate.tcp.SubService;
import cn.foxio.gate.tcp.data.InnerMessage;

/**
 * 发送者
 * @author lucky
 *
 */
public class SenderHelper {

	
	static private ConcurrentHashMap<String, Object> protoxy = new ConcurrentHashMap<>();
	
	static public void saveToProtoxy(String key , Object value) {
		protoxy.put(key, value);
	}
	
	static public <T> T getByProtoxy( String key , Class<T> t ) {
		return (T) protoxy.get(key);
	}
	
	/**
	 * 单发客户端
	 * @param userId
	 * @param msg
	 */
	public static void sendToClient( String serverName ,long userId , IMsg msg ){
		InnerMessage box = new InnerMessage();
		box.setUserId(userId);
		box.setDataContent(msg.toByteArrays());
		box.setCmdId( msg.getCmdId() );
		get(serverName).send(box);
	}
	
	
	static private ConcurrentHashMap<String, SubService> map = new ConcurrentHashMap<>();
	

	
	
	static public void register( String key , SubService service )
	{
		map.put(key, service);
	}
	
	
	/**
	 * key值可以使用 {@link cn.foxio.gate.global.ServerName ServerName}
	 * @param key
	 * @return
	 */
	static public SubService get( String key ){
		return map.get(key);
	}
	
	static public InnerMessage getPublishLMessageBase( int cmdId  , IMsg msgContent ) {
		InnerMessage msg = new InnerMessage();
		msg.setCmdId(cmdId);
		msg.setMainId(String.valueOf(msgContent.getModuleId()));
		msg.setUserId(0);
		msg.setType(MessageTypeDef.Publish.getValue());
		msg.setDataContent(msgContent.toByteArrays());
		return msg;
	}
	
}
