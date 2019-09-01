package cn.foxio.simple.client;

import java.util.UUID;

import org.java_websocket.client.WebSocketClient;

import lombok.Data;

/**
 * 客服端消息适配
 * 
 * @author Administrator
 *
 */
@Data
public class ClientAccpet {
	
	private WebSocketClient client;
	
	
	private int id;
	
	
	public ClientAccpet(WebSocketClient client , int id) {
		this.client = client;
		this.id = id;
	}

	public void handler(String msg) {

		// System.out.println("stc :" + msg);

		

	}
	
	
	public void close(){
		
	}

	

	public void send(String json) {
		// System.out.println(" => cts: " + json);
		try {
			client.send(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAutoToken() {
		return UUID.randomUUID().toString();
	}

	public String getDevice() {
		return UUID.randomUUID().toString();
	}

}
