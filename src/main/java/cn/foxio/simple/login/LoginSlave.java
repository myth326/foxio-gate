package cn.foxio.simple.login;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.face.AbstractAccepter;
import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.face.IPlayerCommand;
import cn.foxio.gate.helper.SenderHelper;
import cn.foxio.gate.tools.ProtoBufUtils;
import cn.foxio.simple.cmd.CmdLoginCts;
import cn.foxio.simple.config.ServerName;
import cn.foxio.simple.msg.LoginCtsMsg;
import cn.foxio.simple.msg.TypeError;


/**
 * 登陆线程
 * 
 * @author lucky
 *
 */
public class LoginSlave extends AbstractAccepter<IMessageBox> {
	

	/**
	 * 绑定的玩家map
	 */
	private ConcurrentHashMap<Long, String> uidMap = new ConcurrentHashMap<>();


	
	/**
	 * 玩家绑定到线程【保证同一玩家，一定是同一个线程来处理】
	 * @param userId
	 * @return
	 */
	public boolean inUser(long userId) {
		if (userId <=0 ) {
			return false;
		}
		return uidMap.get(userId) != null;
	}
	/**
	 * 玩家解绑该线程【保证同一玩家，一定是同一个线程来处理】
	 * @param userId
	 */
	public void outUser(long userId) {
		uidMap.remove(userId);
	}

	public void putUser(long userId) {
		uidMap.put(userId, String.valueOf(userId));
	}

	public int getMsgTotal() {
		return queue.size();
	}
	public LoginSlave(int id) {
		super("LoginActor-" + id);
		
		
		this.start();
	}
	
	/**
	 * 在线玩家数量
	 * @return
	 */
	public int getOnlinePlayer() {
		return uidMap.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected boolean handlerMsg(IMessageBox msg) {
		
		if (GatewayDef.CLIENT_OFF_LINE.equals(msg.getMainId())) {
			
			return true;
		}else if ( GatewayDef.REPEAT_LOGIN.equals(msg.getMainId())) {
			return true;
		}
	
		try {

			IPlayerCommand command = null;
			if ( msg.getCmdId() ==  301 ){
				
				command = new CmdLoginCts();
				command.execute(msg, ProtoBufUtils.decode(LoginCtsMsg.class, msg.getDataContent()));
			}
			
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			SenderHelper.get(ServerName.LOGIN_KEY).send(msg.getUserId(), TypeError.ERROR_ACCOUNT.toErrorMsg("server error"));
		}
		return false;
	}

	
	
	@Override
	public String getServerName() {
		return ServerName.LOGIN_KEY;
	}
	
	
	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
