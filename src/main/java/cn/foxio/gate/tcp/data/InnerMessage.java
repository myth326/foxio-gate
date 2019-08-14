package cn.foxio.gate.tcp.data;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.google.gson.annotations.Expose;

import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.tools.GsonUtil;
import lombok.Data;

/**
 * 网关与子服 之间通讯所用的内部消息
 * 
 * 消息数据结构体 client 只需要 fieldType = 3 , cmdId 命令号必需填写 , dataContent 为定义协议的
 * protobuf二进制数组 ,其它项可以不理会
 * @author lucky
 *
 */
@Data
public class InnerMessage implements IMessageBox {

	/**
	 * 消息类型  client = 5;
	 */
	@Protobuf(order = 1 , fieldType = FieldType.INT32 )
	protected int type;
	/**
	 * 主要标识    模块ID
	 */
	@Protobuf(order = 2 , fieldType = FieldType.STRING )
	protected String mainId;
	/**
	 * 次要标识   该模块的特殊特征【 可具备唯一性 】， 如游戏模块之房间号
	 */
	@Protobuf(order = 3 , fieldType = FieldType.STRING )
	protected String subId;
	
	/**
	 * 消息数据, 定义的协义结构先转成 protobuf byte arrays 存入该字段，再将该结构转成 protobuf byte arrays
	 */
	@Protobuf(order = 4 , fieldType = FieldType.BYTES  )
	@Expose(serialize = false)
	protected byte[] dataContent;

	
	@Protobuf(order = 5  , fieldType = FieldType.STRING )
	/**
	 * ip:port
	 */
	protected String key;
	/**
	 * 玩家id
	 */
	@Protobuf(order = 6 , fieldType = FieldType.INT64 )
	protected long userId;
	/**
	 * 消息协议号
	 */
	@Protobuf( order = 7 , fieldType = FieldType.INT32 )
	protected int cmdId;
	/**
	 * 消息体，该项不打 protobuf包
	 */
	protected Object msgData;
	@Protobuf(order = 8, fieldType = FieldType.STRING )
	protected String gameId;
	
	/**
	 * 商户ID
	 */
	@Protobuf( order = 9 , fieldType = FieldType.INT32 )
	protected int customerId;

	public InnerMessage() {
	}

	/**
	 * 消息数据结构体基础类
	 * 
	 * @param mailId
	 *            主要标识
	 * @param subId
	 *            次要标识
	 * @param DataJson
	 *            消息数据
	 */
	public InnerMessage(String mainId, String subId, byte[] dataContent) {

		this.mainId = mainId;
		this.subId = subId;
		this.dataContent = dataContent;
	}
	

	/**
	 * 消息数据结构体基础类
	 * 
	 * @param mailId
	 *            主要标识
	 * @param DataJson
	 *            消息数据
	 */
	public InnerMessage(String mainId, byte[] dataContent) {
		this.mainId = mainId;
		this.dataContent = dataContent;
	}


	@Override
	public String toString() {
		return GsonUtil.toJson(this);
	}

	@Override
	public String toJson() {
		return GsonUtil.toJson(this);
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IMessageBox setType(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}
