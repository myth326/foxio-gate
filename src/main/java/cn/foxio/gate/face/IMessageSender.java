package cn.foxio.gate.face;

/**
 * 消息盒子
 * @author lucky
 *
 */
public interface IMessageSender {

	/**
	 * 对客户端发送消息
	 * @param msg
	 */
	public void send(IMessageBox msg);

	/**
	 * 对客户端发送消息
	 * @param userId
	 * @param msgContent
	 */
	public void send( long userId , IMsg msgContent);


	/**
	 * 对客户端发送消息
	 * @param userId
	 * @param cmdId 指令
	 * @param msgContent 消息
	 */
	void send(long userId, int cmdId, IInnerMessage msgContent);
	
	
	/**
	 * 发布消息[一般服务器进程之间通讯，不需要知道具体的接收者]
	 * @param msg
	 */
	public void publish(IMessageBox msg);

	/**
	 * 订阅消息
	 * 
	 * @param mainId
	 * @param subId
	 */
	public void addSubscribe(String mainId, String subId);

	/**
	 * 订阅消息
	 * 
	 * @param mainId
	 * @param subId
	 */
	public void addSubscribe(String mainId);

	/**
	 * 删除 订阅消息
	 * 
	 * @param mainId
	 * @param subId
	 */
	public void removeSubscribe(String mainId, String subId);

	/**
	 * 删除 订阅消息
	 * 
	 * @param mainId
	 * @param subId
	 */
	public void removeSubscribe(String mainId);

	/**
	 * 发送消息
	 * @Description: 发送一个消息
	 * @param @param userId
	 * @param @param cmdId
	 * @param @param msgContent    参数
	 * @return void    返回类型
	 * @throws
	 */
	void send(long userId, int cmdId, byte[] msgContent);

	


}
