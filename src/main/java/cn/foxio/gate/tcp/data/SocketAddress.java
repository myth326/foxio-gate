package cn.foxio.gate.tcp.data;

import cn.foxio.gate.face.IObject;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * socket 地址
 * @author lucky
 *
 */
@Data
@AllArgsConstructor
public class SocketAddress implements IObject{

	private String host;
	private int port;

}
