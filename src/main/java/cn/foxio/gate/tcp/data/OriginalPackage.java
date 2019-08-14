package cn.foxio.gate.tcp.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cn.foxio.gate.face.IObject;
import lombok.Data;

/**
 * 原始消息包
 * @author Lucky
 *
 */
@Data
public class OriginalPackage implements IObject{

	
	
	/** 协议类型  short */
	protected int cmdId;
	
	/** 协议包体字节数 */
	protected int bodySize;

	/** 加密用 */
	protected short key;
	
	/**
	 * 二进制数据 protobuf
	 */
	private byte[] protoData;
	
	
	
	public byte[] getProtoData() {
		return protoData;
	}

	public void setProtoData(byte[] protoData) {
		this.protoData = protoData;
	}

	/**
	 * 是否已经读了包头
	 */
	private boolean readHead = false;
	
	
	@Override
	public String toString() {
		return this.toJson();
	}

	public OriginalPackage() {}


	public OriginalPackage(int cmdId, byte[] protoData) {
		super();
		this.cmdId = (short)cmdId;
		this.protoData = protoData;
		if ( protoData != null ) {
			this.bodySize = protoData.length;
		}
	}
	
	public static final int HEAD_SIZE = 8;
	
	public byte[] toByteArrays() {
		
		
		int len = this.getProtoData() == null ? HEAD_SIZE : this.getProtoData().length + HEAD_SIZE;

		ByteBuffer bf = ByteBuffer.allocate(len);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		
		bf.putShort((short) cmdId );
		bf.putInt(bodySize);
		bf.putShort(key);
		
		if ( protoData != null ) {
			bf.put(protoData);
		}
		byte [] bytes = bf.array();
		return bytes;
	}
	
	/**
	 * 读取数据【用于websocket,已经分好包的数据】
	 * @param bytes
	 * @return
	 */
	public boolean readBytes( byte[] bytes)
	{
		if ( bytes == null ) {
			return false;
		}
		ByteBuffer bf = ByteBuffer.allocate(bytes.length);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.put(bytes);
		bf.position(0);
		
		if ( bytes.length < HEAD_SIZE ) {
			return false;
		}
		
		this.cmdId = bf.getShort();
		this.bodySize = bf.getInt();
		this.key = bf.getShort();
		
		if ( bytes.length < bodySize + HEAD_SIZE ) {
			//System.err.println("包体长度不够，长度因当不包括包头!");
			return false;
		}
		
		byte[] body = new byte[bodySize];
		bf.get(body);
		this.setProtoData( body) ;
		return true;
	}
	
}
