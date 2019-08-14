package cn.foxio.gate.def;

/**
 * 消息类型
 * 
 * @author lucky
 *
 */
public enum MessageTypeDef {

	// 心跳
	HeartBeat(-1, "心跳")
	// 握手
	, Handshake(0, "握手"),
	// 订阅
	Subscribe(1, "订阅")
	// 取消订阅
	, UnSubscribe(2, "取消订阅")
	// 发布
	, Publish(3, "发布"), 
	
	SendToClient(4, "发送给客户端"),
	
	ClientToService(5, "客户端发服务端");

	private final int value;
	private final String desc;

	public int getValue() {
		return value;
	}

	public String getDesc() {
		return desc;
	}

	MessageTypeDef(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "value: " + value + ", desc : " + desc;
	}

	public static void main(String[] args) {

		System.out.println(MessageTypeDef.UnSubscribe);
	}
}
