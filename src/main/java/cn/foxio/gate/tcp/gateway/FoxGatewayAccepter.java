package cn.foxio.gate.tcp.gateway;

import org.apache.log4j.Logger;

import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.def.MessageTypeDef;
import cn.foxio.gate.face.AbstractGateAccepter;
import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.helper.ClientOnlineHelper;
import cn.foxio.gate.helper.FoxSubscribeHelper;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.service.FoxBaseTcpService;
import cn.foxio.gate.tools.NettyUtils;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理客户端
 * @author lucky
 *
 */
public class FoxGatewayAccepter extends AbstractGateAccepter {

	
	private Logger logger = Logger.getLogger(FoxGatewayAccepter.class);

	
	
	/**
	 * 订阅辅助器
	 */
	private FoxSubscribeHelper helper = null;
	
	private String gateKey;

	public FoxGatewayAccepter() {
		this.helper = FoxSubscribeHelper.getIstance();
		//this.gateKey = gateKey;
		
	}
	

	@Override
	protected boolean handlerMsg(IMessageBox msg) {
		int type = msg.getType();
		try {
			if (GatewayDef.USER_ID_CHANGE.equals(msg.getMainId())) {
				// 新id
				long newUid = Long.valueOf(msg.getSubId());
				// 原id
				long oldUid = msg.getUserId();
				if (newUid == oldUid) {
					return true;
				}
				ChannelHandlerContext ctxOld = NettyUtils.getCtx(oldUid);
				ChannelHandlerContext ctxNew = NettyUtils.getCtx(newUid);
				if (ctxNew != null) {
					// 帐号已经登录过了 , 踢下线;
					interrupt(ctxNew, 0L);
				}
				// 变更id;
				NettyUtils.changeUid(ctxOld, newUid , msg.getCustomerId());
				ClientOnlineHelper.getIst().toOnline(newUid);
				return true;
			} else if (GatewayDef.TABLE_ID_CHANGE.equals(msg.getMainId())) {
				String tableId = msg.getSubId();
				ctx = NettyUtils.getCtx(msg.getUserId());
				NettyUtils.setPlayerTableId(ctx, tableId);
				tableId = NettyUtils.getTableId(ctx);
			}else if (GatewayDef.REPEAT_LOGIN.equals(msg.getMainId())) {
				logger.info("排重  GatewayDef.RepeatLogin: " + msg.toJson());
				// 重复登陆,将前一个登陆玩家抗下线!
				long uid = Long.valueOf(msg.getSubId());
				ChannelHandlerContext ctx = NettyUtils.getCtx(uid);
				if (ctx != null) {
					NettyUtils.closeCtxConnect(ctx);
				}
			}
			if (type == MessageTypeDef.SendToClient.getValue()) {
				// 发送给客户端 消息;
				long uid = msg.getUserId();
				ChannelHandlerContext ctx = NettyUtils.getCtx(uid);
				if (ctx != null && ! NettyUtils.isClose(ctx)) {
					if ( NettyUtils.isClose(ctx) ) {
						logger.info("客户端已经下线! userId = " + uid + " , 网关广播全部服务器该玩家下线!");
						interrupt(ctx, uid);
					} else {
						NettyUtils.sendMsgToClient(ctx, msg);
					}
				}else {
					// 客户端找不到,可能已经下线!
					logger.info("客户端找不到,可能已经下线! userId = " + uid + " , 网关广播全部服务器该玩家下线!");
					interrupt(ctx, uid);
				}
				return true;
			}
			if (type == MessageTypeDef.ClientToService.getValue()) {
				helper.publish(msg, NettyUtils.getServerMap());
				return true;
			}
			if (type == MessageTypeDef.Publish.getValue()) {
				helper.publish(msg, NettyUtils.getServerMap());
				return true;
			}
			if (type == MessageTypeDef.Subscribe.getValue()) {
				helper.addSubscribe(msg);
				return true;
			}
			if (type == MessageTypeDef.UnSubscribe.getValue()) {
				helper.removeSubscribe(msg);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 临时连接，起始ID//20亿
	 */
	private final int tempConnectStartId = 20 * 10000 * 10000;

	

	@Override
	public void acceptMsg(IMessageBox msg, ChannelHandlerContext ctx) {

		
			
			if (msg.getType() == MessageTypeDef.ClientToService.getValue()) {
			long uid = NettyUtils.getUserId(ctx);

			if (uid > 0) {
				msg.setUserId(uid);

			} else {
				
				String logInfo = "客户端未握手就发送消息" + NettyUtils.getKey(ctx) + " , 通知错误后,服务器主动断开连接!";
				System.err.println(logInfo);
				logger.error(logInfo);
				try {
					interrupt(ctx, 0L);
				} catch (Exception e) {
					System.out.println( logInfo + e );
					e.printStackTrace();
				}

			}
		}
		super.acceptMsg(msg, ctx);
	}
	
	/**
	 * 连接中断
	 * 
	 * @param ctx
	 */
	public synchronized void interrupt(ChannelHandlerContext ctx, Long uid) {
		
		if ( uid == -1 ) {
			return; //检测client , 忽略
		}
		//System.err.println("断线：uid:" + uid + " ip/port , ctx = " + (ctx != null ? NettyUtils.getKey(ctx) : "null"));

		if (uid == null) {
			uid = 0L;
		}

		NettyUtils.unRegAlls(ctx);
		NettyUtils.removeUid(ctx, uid);
		NettyUtils.closeCtxConnect(ctx);

		if (uid < tempConnectStartId && uid > 0) {
			// 通知所有的服务器，玩家下线
			boolean isOnline = ClientOnlineHelper.getIst().isOnline(uid);
			// 只在原来已经在线的连接通知, 下线的id 不作通知. 保证只通知一次
			if (isOnline) {
				String tableId = NettyUtils.getTableId(ctx);
				InnerMessage msg = new InnerMessage(GatewayDef.CLIENT_OFF_LINE, tableId, null);
				msg.setUserId(uid);
				msg.setCustomerId(NettyUtils.getCustomerId(ctx));
				//msg.setGate(gateKey);
				helper.sendCloseToAllService(msg, NettyUtils.getServerMap());
			}
		}
		ClientOnlineHelper.getIst().toDownline(uid);
	}

	
	public String getServerName() {
		return "Gateway";
	}

}
