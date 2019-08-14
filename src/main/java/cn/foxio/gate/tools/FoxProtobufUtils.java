package cn.foxio.gate.tools;

/**
 * 消息工厂编解码
 * @author lucky
 *
 */
public class FoxProtobufUtils  {

	/**
	 * 编码
	 * @param msg
	 * @return
	 */
	static public byte[] encode(Object msg) {
		
		return ProtoBufUtils.encode(msg);
	}

	/**
	 * 解码
	 * @param data
	 * @return
	 */
	public static <T> T decode(Class<T> cls, byte[] data) {
		return ProtoBufUtils.decode(cls, data);
	}

	
	
	
	
	
}
