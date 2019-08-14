package cn.foxio.gate.tcp.data;

import cn.foxio.gate.face.IObject;

/**
 * 通道玩家的状态
 * 
 * @author Lucky
 *
 */
public class FoxPlayerStatus implements IObject {

	private long userId;
	
	//牌桌/区域标识
	private String tableId;

	private String ip;

	private int port;
	//商户ID
	private int customerId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	@Override
	public String toString() {
		return toJson();
	}
}
