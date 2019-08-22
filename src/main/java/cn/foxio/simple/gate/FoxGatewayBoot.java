package cn.foxio.simple.gate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.foxio.gate.tcp.gateway.FoxGateway;
import cn.foxio.gate.tcp.websocket.FoxWsGateway;

/**
 * 启动网关 包含 socket网关与websocket网关 其中 socket网关有两个端口,一个对内通讯,一个对外通讯; websocket对外通讯 ,
 * 兼容 socket与websocket两种协议
 * 
 * @author lucky
 *
 */
@Component
public class FoxGatewayBoot {

	@Value("${gate.socket.inside.port}")
	private int insidePort;

	@Value("${gate.socket.outside.port}")
	private int outsidePort;

	@Value("${gate.websocket.port}")
	private int wsPort;

	public FoxGatewayBoot() {
	}

	public void start() {
		
		try {
			// 启动socket网关
			new FoxGateway(insidePort, outsidePort).start();
			System.out.println(String.format("FoxIo gate boot , socket outsidePort {%s}  insidePort {%s}", outsidePort,
					insidePort));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// 启动websocket网关
			new FoxWsGateway(wsPort).start();
			
			System.out.println(String.format("FoxIo gate boot , websocket port {%s}", wsPort));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}

}
