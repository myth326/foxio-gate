package cn.foxio.gate.helper;



import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩家在线管理
 * @author lucky
 *
 */
public class ClientOnlineHelper {

	
	static private ClientOnlineHelper ist = null;
	
	static public ClientOnlineHelper getIst() {
		if ( ist == null) {
			ist = new ClientOnlineHelper();
		}
		return ist;
	}
	
	
	private ConcurrentHashMap<Long, Integer> userList = new ConcurrentHashMap<>();
	
	/**
	 * 状态变更为在线
	 * @param userId
	 */
	public void toOnline( long userId ) {
		userList.put(userId, 1);
	}
	
	/**
	 * 状态变更为离线
	 * @param userId
	 */
	public void toDownline( long userId) {
		userList.put(userId, 0);
	}
	
	/**
	 * 玩家是否在线
	 * @param userId
	 * @return
	 */
	public boolean isOnline( long userId ) {
		final Integer v = userList.get(userId);
		if ( v != null && v <= 0 ) {
			return false;
		}
		return true;
	}
	
	
	
	
}
