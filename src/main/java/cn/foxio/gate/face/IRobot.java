package cn.foxio.gate.face;

import cn.foxio.gate.tcp.client.FoxTcpClient;

/**
 * 机器人
 * @author lucky
 *
 * @param <T>
 */
public interface IRobot<T> extends IFoxAccepter<T>{

	/**
	 * 客户端
	 * @param client
	 */
	void setTcpClient( FoxTcpClient client);
	/**
	 * 客户端
	 * @return
	 */
	FoxTcpClient getTcpClient();

}
