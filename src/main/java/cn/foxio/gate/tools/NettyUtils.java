package cn.foxio.gate.tools;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import cn.foxio.gate.face.IMessageBox;
import cn.foxio.gate.tcp.data.FoxPlayerStatus;
import cn.foxio.gate.tcp.data.OriginalPackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;

/**
 * netty 工具类
 * @author lucky
 *
 */
public class NettyUtils {

	/**
	 * 服务器终端 对应 ip:port
	 */
	static private ConcurrentHashMap<String, ChannelHandlerContext> serverMap = new ConcurrentHashMap<>();

	/**
	 * 客户端终端 对应 ip:port
	 */
	static private ConcurrentHashMap<String, ChannelHandlerContext> clientMap = new ConcurrentHashMap<>();
	/**
	 * 玩家map 对应 userId
	 */
	static private ConcurrentHashMap<Long, ChannelHandlerContext> userMap = new ConcurrentHashMap<>();
	
	private static Logger logger = Logger.getLogger(NettyUtils.class);

	
	static private AttributeKey<String> ctxKey = AttributeKey.valueOf("ctxKey");
	
	
	static public final String SOCKET = "socket"; 
	static public final String WEB_SOCKET = "websocket"; 
	
	/**
	 * socket or websocket
	 */
	static private AttributeKey<String> ctxProto = AttributeKey.valueOf("ctxProto");

	/**
	 *  long number connectid or userid
	 */
	static private AttributeKey<FoxPlayerStatus> userKey = AttributeKey.valueOf("userKey");
	/**
	 * 发给客户端的消息条数
	 */
	static private AtomicLong toClientMsgNum = new AtomicLong(0);

	/**
	 * 临时连接，起始ID//20亿
	 */
	static public final int TEMP_CONNECT_START_ID = 20 * 10000 * 10000;

	static private AtomicLong sessionId = new AtomicLong();

	static {
		sessionId.set(TEMP_CONNECT_START_ID);
	}

	static public synchronized long newConnectId() {
		long i = sessionId.get();
		i++;
		long max = TEMP_CONNECT_START_ID + 10000000;
		long resetId = TEMP_CONNECT_START_ID + 1000;
		if (i > max ) {
			i = resetId;
		}
		sessionId.set(i);
		return i;
	}

	/**
	 * 服务器Map
	 * 
	 * @return
	 */
	static public ConcurrentHashMap<String, ChannelHandlerContext> getServerMap() {
		return serverMap;
	}

	/**
	 * 注册客户端连接
	 * 
	 * @param ctx
	 */
	static public void regClient(ChannelHandlerContext ctx) {
		if (ctx == null) {
			String info = " NettyUtils.regClient ctx == null ";
			logger.info(info);
			System.err.println(info);
		} else {
			clientMap.put(getKey(ctx), ctx);
			addLog();
		}
	}
	
	
	static private long logActTime = 0;
	
	static private void addLog() {
		
		long curr = System.currentTimeMillis();
		
		//记录间隔
		int diff = 30 * 1000;
		
		//时间大于10 秒才会记录
		if ( curr - logActTime >  diff ) {
			
			String memory = "memory:";
			memory += "Total="+ Runtime.getRuntime().totalMemory()/(1024*1024)+"M";
			memory +=",Free="+ Runtime.getRuntime().freeMemory()/(1024*1024)+"M";
			memory +=",Max="+ Runtime.getRuntime().maxMemory()/(1024*1024)+"M";
			memory +=",Used="+(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024)+"M";

			
			Iterator<Entry<String, ChannelHandlerContext>> iterator = clientMap.entrySet().iterator();
			//websocket  连接数量
			int wsNum = 0;
			try {
				while( iterator.hasNext() )
				{
					Entry<String, ChannelHandlerContext> obj = iterator.next();
					String k = obj.getValue().channel().attr(ctxProto).get();
					if ( WEB_SOCKET.equals( k )) {
						wsNum++;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				logger.info(e);
			}
			
			//内存
			//logger.info(memory);
			//连接
			logger.info(memory + " .  clientMap size = " + clientMap.size() 
						+ " , userMap size = " + userMap.size() 
						+ " , ws num =" + wsNum
						+ " , toClientMsgNum = " + toClientMsgNum.get() 
					);
		
			logActTime = curr;
		}
		
		
	}
	

	/**
	 * 注册服务器连接
	 * 
	 * @param ctx
	 */
	static public void regServer(ChannelHandlerContext ctx) {
		if (ctx == null) {
			String info = " NettyUtils.regServer ctx == null ";
			logger.info(info);
			System.err.println(info);
		} else {
			serverMap.put(getKey(ctx), ctx);
		}
	}

	/**
	 * 取消注册所有, [断开连接使用]
	 * 
	 * @param ctx
	 */
	static public void unRegAlls(ChannelHandlerContext ctx) {
		if (ctx == null) {
			String info = " NettyUtils.unRegAlls ctx == null ";
			logger.info(info);
			System.err.println(info);
		} else {
			String key = getKey(ctx);
			if ( isService(ctx)) {
				serverMap.remove(key);
			}else {
				clientMap.remove(key);
			}
			addLog();
		}
	}

	/**
	 * 自动分配id
	 * 
	 * @param ctx
	 * @return
	 */
	static public long autoRegUid(ChannelHandlerContext ctx) {

		if (ctx != null && ctx.channel() != null) {

			final long v = getUserId(ctx);
			if (v > 0) {
				System.err.println(" ctx 已经有了一个 uid , uid= " + v + " , 即将重新注册 uid, 请检测逻辑是否有误 !");
			}

			final long uid = newConnectId();
			getPlayerStatus(ctx).setUserId(uid);
			userMap.put(uid, ctx);
			return uid;
		} else {
			return -1;
		}
	}

	/**
	 * uid变更
	 * 
	 * @param ctx
	 * @param newUid
	 */
	static public void changeUid(ChannelHandlerContext ctx, long newUid , int customerId) {
		if (ctx != null && ctx.channel() != null) {
			final long old = getUserId(ctx);
			userMap.remove(old);
			userMap.put(newUid, ctx);
			getPlayerStatus(ctx).setUserId(newUid);
			getPlayerStatus(ctx).setCustomerId(customerId);
		}
	}

	/**
	 * 移除uid注册信息;
	 * 
	 * @param ctx
	 * @param uid
	 */
	static public void removeUid(ChannelHandlerContext ctx, long uid) {
		ChannelHandlerContext getCtx = userMap.get(uid);
		if (ctx == getCtx) {
			userMap.remove(uid);
		} else {

			Iterator<Entry<Long, ChannelHandlerContext>> iterator = userMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Long, ChannelHandlerContext> entry = iterator.next();
				ChannelHandlerContext data = entry.getValue();
				Long key = entry.getKey();
				if (ctx == data) {
					userMap.remove(key);
					return;
				}
			}
			//String info = "NettyUtils.UID_Remove () 移除错误 ! , uid=" + uid;
			//logger.info(info);
			//System.err.println(info);
		}
	}

	static public FoxPlayerStatus setPlayerTableId(ChannelHandlerContext ctx, String tableId) {
		FoxPlayerStatus cps = getPlayerStatus(ctx);
		if (cps != null) {
			cps.setTableId(tableId);
		}
		return cps;
	}
	
	static public String getTableId(ChannelHandlerContext ctx) {
		FoxPlayerStatus cps = getPlayerStatus(ctx);
		if (cps != null) {
			return cps.getTableId();
		}
		return null;
	}
	
	static public int getCustomerId(ChannelHandlerContext ctx) {
		FoxPlayerStatus cps = getPlayerStatus(ctx);
		if (cps != null) {
			return cps.getCustomerId();
		}
		return -1;
	}


	static public FoxPlayerStatus getPlayerStatus(ChannelHandlerContext ctx) {

		if (ctx != null && ctx.channel() != null) {
			FoxPlayerStatus v = ctx.channel().attr(userKey).get();
			if ( v == null) {
				ctx.channel().attr(userKey).set(new FoxPlayerStatus());
			}
			return v;
		}
		return null;
	}
	
	/**
	 * 设置ctxProto, socket/websocket
	 * 
	 * @param ctx
	 * @param key
	 * @return
	 */
	static public String setProto(ChannelHandlerContext ctx, String key) {
		if (ctx != null && ctx.channel() != null) {
			ctx.channel().attr(ctxProto).set(key);			
			return key;
		} else {
			String err = "NettyUtils.setProto (ctx , key )  ctx is error ";
			logger.info(err);
			System.err.println(err);
			return err;
		}
	}
	
	/**
	 * 设置key, IP:PORT
	 * 
	 * @param ctx
	 * @param key
	 * @return
	 */
	static public String setKey(ChannelHandlerContext ctx, String key) {
		if (ctx != null && ctx.channel() != null) {
			ctx.channel().attr(ctxKey).set(key);
			return key;
		} else {
			String err = "NettyUtils.setKey (ctx)  ctx is error ";
			logger.info(err);
			System.err.println(err);
			return err;
		}
	}

	/**
	 * 自动设置key, IP:port格式
	 * 
	 * @param ctx
	 * @return
	 */
	static public String autoKey(ChannelHandlerContext ctx) {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = insocket.getAddress().getHostAddress();
		int port = insocket.getPort();
		String key = ip + ":" + port;
		return setKey(ctx, key);
	}

	/**
	 * 是否为子服务器
	 * 
	 * @param ctx
	 * @return
	 */
	static public boolean isService(ChannelHandlerContext ctx) {
		final String key = getKey(ctx);
		if (key != null && serverMap.get(key) != null) {
			return true;
		}
		return false;
	}

	/**
	 * 取得玩家id / connectid
	 * 
	 * @param ctx
	 * @return
	 */
	static public long getUserId(ChannelHandlerContext ctx) {

		if (ctx != null && ctx.channel() != null && ctx.channel().attr(userKey) != null) {
			final FoxPlayerStatus cps = getPlayerStatus(ctx);
			if (cps == null) {
				return -1;
			}
			return cps.getUserId();
		}
		return -1;
	}

	static public String getKey(ChannelHandlerContext ctx) {
		if (ctx == null) {
			return " ctx is null";
		}
		return ctx.channel().attr(ctxKey).get();
	}

	/**
	 * 结束连接
	 * 
	 * @param ctx
	 */
	static public void closeCtxConnect(ChannelHandlerContext ctx) {
		if (ctx != null && !isClose(ctx.channel())) {
			ctx.channel().close();
			ctx.close();
		}
		
	}

	/**
	 * 是否已经关闭
	 * 
	 * @param ctx
	 * @return
	 */
	static public boolean isClose(ChannelHandlerContext ctx) {

		if (ctx == null) {
			return true;
		}
		Channel channel = ctx.channel();
		if (channel == null) {
			return true;
		}
		return !channel.isActive() || !channel.isOpen();
	}

	/**
	 * 是否已经关闭
	 * 
	 * @param channel
	 * @return
	 */
	static public boolean isClose(Channel channel) {
		if (channel == null) {
			return true;
		}
		return !channel.isActive() || !channel.isOpen();
	}

	static public ChannelHandlerContext getCtx(long userId) {
		return userMap.get(userId);
	}

	static public void sendMsgToCtx(ChannelHandlerContext ctx, byte[] ba, int cmdId) {
		
		
		OriginalPackage pack = new OriginalPackage(cmdId, ba);
		
		if ( WEB_SOCKET.equals( ctx.channel().attr(ctxProto).get() ) )
		{
			flushWebSocket(ctx, pack.toByteArrays() );
			addLog();
			return;
		}
		try {
			ctx.channel().writeAndFlush(pack);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 发送websocket消息
	 * @param ctx
	 * @param datas
	 */
	static public boolean flushWebSocket(ChannelHandlerContext ctx , byte[] datas )
	{
		
		addLog();
		
		if ( datas == null ){
			logger.error( " flushWebSocket (ctx , datas)  error , datas == null ");
			return false;
		}else if (  isClose(ctx) ){
			logger.error( " flushWebSocket (ctx , datas)  error , ctx isClose !");
			return false;
		}else{
			ByteBuf bf2 = ctx.alloc().buffer(datas.length + 10);
			bf2.writeBytes(datas);
			BinaryWebSocketFrame tws = new BinaryWebSocketFrame( bf2 );
			ctx.channel().writeAndFlush(tws);
			
			toClientMsgNum.set( toClientMsgNum.get() + 1 );
		}
		
		return true;
	}
	
	/**
	 * 转发客户端
	 * @param ctx
	 * @param box
	 */
	static public void sendMsgToClient(ChannelHandlerContext ctx, IMessageBox box) {
		
		toClientMsgNum.set( toClientMsgNum.get() + 1 );
		
		addLog();
		
		OriginalPackage pack = new OriginalPackage(box.getCmdId(), box.getDataContent());
		pack.setCmdId(box.getCmdId());
		if ( WEB_SOCKET.equals( ctx.channel().attr(ctxProto).get() ) )
		{
			flushWebSocket(ctx, pack.toByteArrays() );
			return;
		}
		try {
			if(ctx.channel().isWritable()){
				ctx.channel().writeAndFlush(pack);
            }else{
            	ctx.channel().writeAndFlush(pack).sync();
                logger.info("同步发送");
            }
			//ctx.channel().writeAndFlush(pack);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("sendMsgToClient，发送异常：" + e);
		}
		
	}

}
