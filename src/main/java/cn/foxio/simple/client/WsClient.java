package cn.foxio.simple.client;
 
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.tcp.data.OriginalPackage;
import cn.foxio.simple.msg.LoginCtsMsg;
 
public class WsClient {
 
    
    public  WebSocketClient client;
    
    
    
    public void connect(){
    	client.connect();
    }
    
    
   public void init(String wsUrl ){
    	
    	
        try {
        	
            client = new WebSocketClient(new URI( wsUrl ),new Draft_6455()) {
            //client = new WebSocketClient(new URI("ws://login.yaguanqp.com:888"),new Draft_6455()) {
            	
            	//private ClientAccpet accept = new ClientAccpet(this , id  );
            	
            	private String device = "";
            	
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                	
                	
                	LoginCtsMsg msg = new LoginCtsMsg();
                	msg.setDeviceId(1);
                	msg.setToken(UUID.randomUUID().toString());
                	
                	
                	OriginalPackage pack = new OriginalPackage( msg.getCmdId() , msg.toByteArrays());
                	this.send(pack.toByteArrays());
                	
                }
                
                @Override
                public void onMessage(ByteBuffer bytes){
                	System.out.println("收到消息=========="+bytes);
                }
 
                @Override
                public void onMessage(String msg) {
                	System.out.println("收到消息=========="+msg);
                }
 
                @Override
                public void onClose(int i, String s, boolean b) {
                	System.out.println("链接已关闭" + s);
                }
 
                @Override
                public void onError(Exception e){
                    e.printStackTrace();
                    System.out.println("发生错误已关闭");
                }
                
                
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        
 
        connect();
    }
 
}