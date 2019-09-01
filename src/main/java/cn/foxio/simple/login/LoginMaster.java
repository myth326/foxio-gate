package cn.foxio.simple.login;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.face.AbstractAccepter;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tools.LoggerUtil;
import cn.foxio.simple.config.ServerName;

/**
 * 
 * @author lucky
 *
 */
public class LoginMaster extends AbstractAccepter<InnerMessage> {

	

	protected int threadMax = Runtime.getRuntime().availableProcessors() * 2;

	private ConcurrentHashMap<Integer, LoginSlave> threadList = new ConcurrentHashMap<>();
	
	

	public LoginMaster() {
		super("LoginMaster");
		initThread();
		this.start();
	}

	/**
	 * 初始化线程;
	 */
	protected void initThread() {
		
		int threadNum = Runtime.getRuntime().availableProcessors() <= 4 ? 4 : Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < threadNum; i++) {
			LoginSlave actor = new LoginSlave(i);
			threadList.put(i, actor);
		}
	}

	/**
	 * 取得线程，如线程忙，自动创建
	 * 
	 * @param userId
	 * @return
	 */
	protected synchronized LoginSlave autoGetAccountActor(long userId) {

		LoginSlave actor = null;
		// 最闲线程的序号
		int idleIdx = -1;
		// 最闲线程的未处理条数;
		int minMsgTotal = 9999;
		for (int i = 0; i < threadList.size(); i++) {
			actor = threadList.get(i);
			if (actor == null) {

			} else if (actor.inUser(userId)) {
				return actor;
			} else {
				int n = actor.getMsgTotal();
				if (n < minMsgTotal) {
					minMsgTotal = n;
					idleIdx = i;
				}
			}
		}
		int threadNum = threadList.size();
		
		// 最闲线程的消息有2条以上 ，新增加一个线程;
		if (minMsgTotal > 1 && threadNum < threadMax) {
			actor = new LoginSlave(threadNum);
			actor.inUser(userId);
			threadList.put(threadNum, actor);
		} else {
			// 新的玩家，给最闲的线程去处理
			actor = threadList.get(idleIdx);
			actor.inUser(userId);
		}
		return actor;
	}

	@Override
	protected boolean handlerMsg(InnerMessage msg) {
		
		long userId = -1;

		// 特殊处理的部分
		if (GatewayDef.CLIENT_OFF_LINE.equals(msg.getMainId())) {
			// 玩家下线，
			userId = msg.getUserId();
			getLogger().info("玩家下线，userId = " + userId);
			//将玩家下线
			LoginSlave actor = autoGetAccountActor(userId);
			actor.outUser(userId);
			
			logDownLine(userId);
			return true;
		}
		
		

		userId = msg.getUserId();
		LoginSlave actor = autoGetAccountActor(userId);

		if (actor != null) {
			actor.acceptMsg(msg, null);
		} else {
			getLogger().warn(" LoginMaster.handlerMsg 有一条消息没有处理！");
		}
		
		
		return true;
	}
	
	
	

	@Override
	public String getServerName() {
		return ServerName.LOGIN_KEY;
	}

	/**
	 * 新玩家下线日志
	 */
	private void logDownLine(long userId) {
		
	}
	

	@Override
	protected Logger getLogger() {
		return LoggerUtil.getLoggerByName("LoginMaster");
	}
	
	
	

}
