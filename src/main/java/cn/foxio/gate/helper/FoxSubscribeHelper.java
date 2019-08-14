package cn.foxio.gate.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.tcp.SubServiceBase;
import cn.foxio.gate.tcp.data.SubscribeData;
import cn.foxio.gate.tools.GsonUtil;
import cn.foxio.gate.tools.NettyUtils;
import io.netty.channel.ChannelHandlerContext;

/**
 * 订阅帮助类
 * @author lucky
 *
 */
public class FoxSubscribeHelper {
	
	
	static private FoxSubscribeHelper instance = new FoxSubscribeHelper();
	static public FoxSubscribeHelper getIstance() {
		return instance;
	}
	
	private FoxSubscribeHelper() {
		
	}

	/** 订阅数据 key 服务器ip:port value 订阅数据*/
	private ConcurrentHashMap<String, SubscribeData> map = new ConcurrentHashMap<>();

	private Logger logger = Logger.getLogger(SubServiceBase.class);

	
	/**
	 * 添加订阅
	 * 
	 * @param msg
	 */
	public void addSubscribe(IMessageBox msg) {

		
		String key = msg.getKey();
		SubscribeData d = map.get(key);
		if (d == null) {
			d = new SubscribeData();
			d.setCreateDate(new Date());
			d.setKey(key);
			map.put(key, d);
		}
		// 主标识
		String m = msg.getMainId();
		// 次标识
		String s = msg.getSubId();
		if (GatewayDef.SUBSCRIPTION_ID_DEFAULT.equals(s) || s == null) {
			d.getMsgMap().put(m, new Object());
		} else {
			d.getMsgMap().put(m + "." + s, new Object());
		}
		
		logger.info( String.format("子服订阅: m[%s],s[%s],key[%s]",m,s,key) );
		
		logger.info( String.format("当前订阅数据:%s", GsonUtil.toJson(map)) );
		
		
		//清理已经断线的订阅数据
		
		List<String> removeList = new ArrayList<>();
		
		ConcurrentHashMap<String, ChannelHandlerContext> serverMap = NettyUtils.getServerMap();

		Iterator<Entry<String, SubscribeData>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, SubscribeData> entry = iterator.next();
			SubscribeData data = entry.getValue();
			if ( serverMap.get( data.getKey() ) == null ) {
				removeList.add( entry.getKey() );
			}
		}
		
		//批量清理
		for ( String k : removeList ){
			map.remove(k);
			logger.info( "	-清理失效的订阅数据,key=" + k);
		}
		
	}
	
	
	public void removeSubscribe(String key) {
		
		List<String> removeList = new ArrayList<>();
		Iterator<Entry<String, SubscribeData>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, SubscribeData> entry = iterator.next();
			if ( entry.getKey().equals(key)) {
				removeList.add( entry.getKey() );
			}
		}
		//批量清理
		for ( String k : removeList ){
			map.remove(k);
			logger.info( "	removeSubscribe -清理失效的订阅数据,key=" + k);
		}
	}

	/**
	 * 取消订阅
	 * 
	 * @param msg
	 */
	public void removeSubscribe(IMessageBox msg) {
		String key = msg.getKey();
		SubscribeData d = map.get(key);
		if (d == null) {
			return;
		}
		// 主标识
		String m = msg.getMainId();
		// 次标识
		String s = msg.getSubId();
		if (GatewayDef.SUBSCRIPTION_ID_DEFAULT.equals(s) || s == null) {
			key = m;
		} else {
			key = m + "." + s;
		}
		d.getMsgMap().remove(key);
		
		logger.info( String.format("取消订阅: m[%s],s[%s],key[%s]",m,s,msg.getKey()) );
	}

	/**
	 * 广播消息给所有服务器
	 * 
	 * @param msg
	 */
	public void sendToAllService(IMessageBox msg, ConcurrentHashMap<String, ChannelHandlerContext> channelMap) {

		Iterator<Entry<String, SubscribeData>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {

			Entry<String, SubscribeData> entry = iterator.next();
			//LSubscribeData data = entry.getValue();
			String key = entry.getKey();
			// 发送消息给订阅者;
			ChannelHandlerContext ctx = channelMap.get(key);
			if (ctx != null) {
				sendToCtx(ctx, msg);
			} else {
				// 异常
				logger.info("订阅者没有找到. msg = " + msg.toJson());
				logger.info("当前订阅数据:" + GsonUtil.toJson(map));
				logger.info("当前All Service Channel :: "+getKeyLst(channelMap));

			}

		}
	}

	/**
	 * 发送离线消息到各个服务器
	 */
	public void sendCloseToAllService(IMessageBox msg, ConcurrentHashMap<String, ChannelHandlerContext> channelMap) {

		Iterator<Entry<String, SubscribeData>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {

			Entry<String, SubscribeData> entry = iterator.next();
//			SubscribeData data = entry.getValue();
//			if (data.getMsgMap() != null && data.getMsgMap().size() > 0) {
//				String tmp = data.getMsgMap().keys().nextElement();
//				int index = tmp.lastIndexOf(".");
//				if (index != -1) {
//					String subId = tmp.substring(index + 1, tmp.length());
//					msg.setGameId(subId);
//					;
//				}
//			}

			String key = entry.getKey();
			// 发送消息给订阅者;
			ChannelHandlerContext ctx = channelMap.get(key);
			if (ctx != null) {
				sendToCtx(ctx, msg);
			} else {
				// 异常
				logger.info("订阅者没有找到. msg = " + msg.toJson());
				logger.info("当前订阅数据:" + GsonUtil.toJson(map));
				logger.info("当前All Service Channel :: "+getKeyLst(channelMap));
			}

		}
	}
	
	
	private String getKeyLst(ConcurrentHashMap<String, ChannelHandlerContext> channelMap ) {
		
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<String, ChannelHandlerContext>> it = channelMap.entrySet().iterator();
		while( it.hasNext() ) {
			Entry<String, ChannelHandlerContext> entry = it.next();
			sb.append("," + entry.getKey() );
		}
		return sb.toString();
	}
	

	/**
	 * 广播消息给所有订阅者
	 * 
	 * @param msg
	 */
	public void publish(IMessageBox msg, ConcurrentHashMap<String, ChannelHandlerContext> channelMap) {

		String main = msg.getMainId();
		String sub = msg.getSubId();

		boolean res = false;
		String allDef = "0";
		if (sub == null || GatewayDef.SUBSCRIPTION_ID_DEFAULT.equals(sub) || sub == "" || sub.equals(allDef)) {
			// 无订阅子标识;
			
			ChannelHandlerContext ctx = getIdexServer(main, channelMap);
			res = sendToCtx(ctx, msg);
			

			if (!res) {
				logger.error("消息发布,没有找到相关订阅者 : msg = " + msg.toJson());
			}

			return;
		} else {
			// 有订阅子标识;

			Iterator<Entry<String, SubscribeData>> iterator = map.entrySet().iterator();
			int n = 0;
			while (iterator.hasNext()) {

				Entry<String, SubscribeData> entry = iterator.next();
				SubscribeData data = entry.getValue();
				//就否有订阅
				boolean isSub = false;
				isSub = data.isSubscribe(main, sub);
				if (isSub) {
					String key = entry.getKey();
					// 发送消息给订阅者;
					ChannelHandlerContext ctx = channelMap.get(key);
					if (ctx != null) {
						sendToCtx(ctx, msg);
						n++;
					} else {
						// 异常channel 为空###,数据发送失败
						logger.error("有一条消息，订阅者者已经不存在. key="+key+", msg = " + msg.toJson());
						
						logger.error("channelMap=" + getKeyLst(channelMap) );
					}
				}
			}
			if (n == 0) {
				String errMsg = "有一条消息，没有订阅者. msg = " + msg.toJson();
				logger.error(errMsg);
				System.out.println(errMsg);
				logger.info("当前订阅数据:" + GsonUtil.toJson(map));
				logger.info("当前All Service Channel :: "+ getKeyLst(channelMap));
			}else if ( n > 1 ) {
				logger.info("发给多["+n+"]个订阅者:" + msg.toJson() );
			}
		}
	}

	/**
	 * 取得订阅 mainId 消息的最闲的服务器
	 * 
	 * @param mainId
	 * @param channelMap
	 * @return
	 */
	private ChannelHandlerContext getIdexServer(String mainId,
			ConcurrentHashMap<String, ChannelHandlerContext> channelMap) {
		Iterator<Entry<String, SubscribeData>> iterator = map.entrySet().iterator();

		int size = 0xffffff;

		ChannelHandlerContext res = null;

		while (iterator.hasNext()) {

			Entry<String, SubscribeData> entry = iterator.next();
			SubscribeData data = entry.getValue();
			if (data.isSubscribe(mainId, null)) {

				String key = entry.getKey();
				// 发送消息给订阅者;
				ChannelHandlerContext ctx = channelMap.get(key);
				if (ctx != null) {
					int mapSize = entry.getValue().getMsgMap().size();
					if (mapSize < size) {
						res = ctx;
					}
				}
			}
		}
		return res;
	}

	/**
	 * 取得订阅 mainId 消息的最闲的服务器 
	 * 
	 * @param mainId
	 * @param channelMap
	 * @return
	 */
	private ChannelHandlerContext getIdexServer(String mainId, String gameId,
			ConcurrentHashMap<String, ChannelHandlerContext> channelMap) {
		Iterator<Entry<String, SubscribeData>> iterator = map.entrySet().iterator();

		int size = 0xffffff;

		ChannelHandlerContext res = null;

		while (iterator.hasNext()) {

			Entry<String, SubscribeData> entry = iterator.next();
			SubscribeData data = entry.getValue();
			if (data.isSubscribe(mainId, null, gameId)) {

				String key = entry.getKey();
				// 发送消息给订阅者;
				ChannelHandlerContext ctx = channelMap.get(key);
				if (ctx != null) {
					int s = entry.getValue().getMsgMap().size();
					if (s < size) {
						res = ctx;
					}
				}
			}
		}
		return res;
	}

	

	/**
	 * 发送消息到指定通道
	 * @param ctx
	 * @param msg
	 */
	public boolean sendToCtx(ChannelHandlerContext ctx, IMessageBox msg) {


		if (ctx == null || NettyUtils.isClose(ctx.channel())) {

			String info = "channel 为空###,数据发送失败! cmdid = " + msg.getCmdId() + " msg=" + msg.toJson();
			logger.error(info);
			System.err.println(info);
			return false;
		}
		
		if ( NettyUtils.isService(ctx)) {
			NettyUtils.sendMsgToCtx(ctx, msg.toByteArrays() ,msg.getCmdId());
		}else {
			NettyUtils.sendMsgToClient(ctx, msg);
		}

		return true;
	}

}
