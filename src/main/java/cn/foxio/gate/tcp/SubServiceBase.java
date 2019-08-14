package cn.foxio.gate.tcp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.face.IInnerMessage;
import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.face.IMessageSender;
import cn.foxio.gate.face.IMsg;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.service.FoxTcpService;
import cn.foxio.gate.tools.PlayerOnlineManager;

/**
 * 内部子服务器
 * @author lucky
 *
 */
public class SubServiceBase implements IMessageSender {

	
	private Logger logger = Logger.getLogger(SubServiceBase.class);

	
	protected ArrayList<FoxTcpService> clientList = new ArrayList<>();

	
	@Override
	public void publish(IMessageBox msg) {
		msg.setType( MessageTypeDef.Publish.getValue() );
		toAlls(msg);
	}
	
	protected void toAlls( IMessageBox msg )
	{
		bindMainId(msg);
		for (FoxTcpService client : clientList) {
			if (client != null){
				client.sendStringMsg(msg);
			}
		}
	}
	
	/**
	 * 订阅，取消订阅的列表
	 */
	private ConcurrentLinkedQueue<InnerMessage> subScribeLst = new ConcurrentLinkedQueue<>();
	
	public ConcurrentLinkedQueue<InnerMessage> getSubScribeLst() {
		return subScribeLst;
	}

	@Override
	public void addSubscribe(String mainId, String subId) {

		InnerMessage msg = new InnerMessage(mainId, subId, null );
		msg.setType( MessageTypeDef.Subscribe.getValue() );
		msg.setCmdId(MessageTypeDef.Subscribe.getValue());
		System.err.println("订阅:" + mainId + "." +subId );
		subScribeLst.add(msg);
		
		toAlls(msg);
	}
	
	

	@Override
	public void addSubscribe(String mainId) {
		addSubscribe(mainId, GatewayDef.SUBSCRIPTION_ID_DEFAULT);
	}
	public void addSubscribe(Integer mainId) {
		addSubscribe(String.valueOf(mainId), GatewayDef.SUBSCRIPTION_ID_DEFAULT);
	}
	public void addSubscribe(long mainId, long subId) {
		addSubscribe(String.valueOf(mainId), String.valueOf(subId));
	}

	@Override
	public void removeSubscribe(String mainId, String subId) {
		InnerMessage msg = new InnerMessage(mainId, subId, null );
		msg.setType( MessageTypeDef.UnSubscribe.getValue() );
		subScribeLst.add(msg);
		toAlls(msg);
	}
	public void removeSubscribe(long mainId, long subId) {
		removeSubscribe(String.valueOf(mainId), String.valueOf(subId));
	}
	

	@Override
	public void removeSubscribe(String mainId) {
		removeSubscribe(mainId, GatewayDef.SUBSCRIPTION_ID_DEFAULT);

	}


	@Override
	public void send(IMessageBox msg) {
		
		if (logger.isDebugEnabled()) {
			//logger.debug("下行： "+ msg.toJson());
		}
		
		bindMainId(msg);
		msg.setType( MessageTypeDef.SendToClient.getValue() );
		
		//System.out.println("多网关情况下，这里需要根据玩家当前所连的 gateway 来发送消息， 该功能待完善！");
		
		for (FoxTcpService client : clientList) {
			if (client != null){
				boolean res = client.sendStringMsg(msg);
				if ( res ) {
					return;
				}
			}
		}
		
	}
	
	/**
	 * 绑定主命令为模块id
	 * @param msg
	 */
	protected void bindMainId(IMessageBox msg) {
		if ( msg.getMainId()== null || msg.getMainId().length() == 0 ) {
			//int mainId = ProtoList.getModuleId(msg.getCmdId());
			msg.setMainId(String.valueOf(msg.getCmdId() / 100 ));
		}
	}
	
	
	
	@Override
	public void send(long userId, IMsg msgContent) {
		
		logger.info("下行: to userId["+userId+"] , cmd["+msgContent.getCmdId()+  "] " );
		
		if ( userId <= 0 ) {
			return;
		}
		
		if (! PlayerOnlineManager.getIst().isOnline(userId)) {
			logger.debug("玩家已下线,不能接收消息  uid = " + userId);
			return;
		}
		
		InnerMessage msg = new InnerMessage();
		msg.setCmdId( msgContent.getCmdId() );
		
		
		
		msg.setUserId(userId);
		msg.setType(MessageTypeDef.SendToClient.getValue());
		msg.setDataContent( msgContent.toByteArrays());
		bindMainId(msg);
		
//		System.out.println("多网关情况下，这里需要根据玩家当前所连的 gateway 来发送消息， 该功能待完善！");
		
		
		for (FoxTcpService client : clientList) {
			if (client != null){
				boolean res = client.sendStringMsg(msg);
				if ( res ) {
					return;
				}
			}
		}
	}
	
	
	@Override
	public void send(long userId, int cmdId, byte[] msgContent) {
//		System.out.println("下行 = > uid= "+userId +"  cmdId="+cmdId+" ["+TimeUtil.getNowDataStr()+"] : " );
		
		//if (logger.isDebugEnabled() && cmdId != 208)
		//	logger.debug("下行 ：> uid="+ userId+",cmdId="+ cmdId);
		
		InnerMessage msg = new InnerMessage();
		msg.setCmdId(cmdId);
		msg.setUserId(userId);
		msg.setType(MessageTypeDef.SendToClient.getValue());
		msg.setDataContent(msgContent);
				
		for (FoxTcpService client : clientList) {
			if (client != null){
				boolean res = client.sendStringMsg(msg);
				if ( res ) {
					return;
				}
			}
		}
	}

	@Override
	public void send(long userId, int cmdId, IInnerMessage msgContent) {
		
//		System.out.println("下行 = > uid= "+userId +" ["+TimeUtil.getNowDataStr()+"] : " + msgContent.toJsonString() );
		
//		if (logger.isDebugEnabled() )
//			logger.debug("下行 ：> uid={"+userId+"} , cmdId:{"+msgContent.getCmdId()
//			+"},  class:{"+msgContent.getClass().getSimpleName()
//			+"}, content:{"+msgContent.toJson()+"}");
		
		InnerMessage msg = new InnerMessage();
		msg.setCmdId(cmdId);
		msg.setUserId(userId);
		msg.setType(MessageTypeDef.SendToClient.getValue());
		msg.setDataContent(msgContent.toByteArrays());
				
		for (FoxTcpService client : clientList) {
			if (client != null){
				boolean res = client.sendStringMsg(msg);
				if ( res ) {
					return;
				}
			}
		}
		
	}
	
	
//	@Override
//	public void send(long userId, ILMessageContent msgContent) {
//		
////		System.out.println("下行 = > uid= "+userId +" ["+TimeUtil.getNowDataStr()+"] : " + msgContent.toJsonString() );
//		
//		if (logger.isDebugEnabled() && msgContent.getCmdId() != 208)
//			logger.debug("下行 ：> uid={} , cmdId:{},  class:{}, content:{}", userId, msgContent.getCmdId(), msgContent.getClass().getSimpleName(), msgContent.toJsonString());
//		
//		LMessageBox msg = new LMessageBox();
//		msg.setCmdId( LMessageFactory.getCmdIdByClass(msgContent.getClass() ));
//		msg.setUserId(userId);
//		msg.setType(TypeLMessage.SendToClient.getValue());
//		msg.setDataContent( msgContent.toByteArrays());
//		
////		System.out.println("多网关情况下，这里需要根据玩家当前所连的 gateway 来发送消息， 该功能待完善！");
//		
//		for (MQTcpService client : clientList) {
//			if (client != null){
//				boolean res = client.sendStringMsg(msg);
//				if ( res ) return;
//			}
//		}
//	}

}
