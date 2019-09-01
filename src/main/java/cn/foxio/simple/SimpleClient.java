package cn.foxio.simple;

import cn.foxio.simple.client.WsClient;

public class SimpleClient {

	
	public static void main(String[] args) {
		
		String wsUrl = "ws://127.0.0.1:7777";
		
		new WsClient().init(wsUrl);
		
		
		
	}
}
