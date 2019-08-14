//package cn.foxio.gate.tcp;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//
//import cn.foxio.gate.face.IFoxAccepter;
//import cn.foxio.gate.face.IRobot;
//import cn.foxio.gate.helper.LoggerHelper;
//import cn.foxio.gate.tcp.client.FoxTcpClient;
//import cn.foxio.gate.tcp.data.InnerMessage;
//import cn.foxio.gate.tcp.data.SocketAddress;
//import cn.foxio.gate.tcp.gateway.FoxGateway;
//import cn.foxio.gate.tcp.websocket.FoxWsGateway;
//import cn.foxio.gate.tools.PropertiesConfig;
//import cn.foxio.gate.tools.ServerUtils;
//import cn.foxio.simple.GlobalConfig;
//
///**
// * 消息队列
// * @author lucky
// *
// */
//public class FoxIO {
//
//	static private boolean isInit = false;
//	
//	static private Logger getLogger() {
//		return LoggerHelper.getLoggerByName("FoxIO");
//	}
//	
//	/**
//	 * 网关地址
//	 */
//	static public ArrayList<SocketAddress> gatewayAddressList;
//
//	/**
//	 * 配置文件的目录;
//	 */
//	static public final String CONFIG_PATH = GlobalConfig.getConfigPath() + "config.properties";
//
//	
//	
//	
//	static private Class<?> getClass(String key) {
//		try {
//
//			String clsName = cfg.getValString(key);
//			if (clsName == null) {
//				return null;
//			}
//			Class<?> cls = Class.forName(clsName);
//			return cls;
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//
//	/**
//	 * 协议解析器类名 客户端
//	 * 
//	 * @return
//	 */
//	static public IFoxAccepter<?> getClientAccepter() {
//		IFoxAccepter<?> accepter = null;
//		try {
//			Class<?> cls = getClass("ClientAccepterClassName");
//			if (cls != null) {
//				accepter = (IFoxAccepter<?>) cls.newInstance();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("定义的 ServiceAccepterClassName 名称不对，无法反射成 extends IMqAccepter 类 ! ");
//		}
//		return accepter;
//	}
//
//	/**
//	 *  配置数据
//	 */
//	static public PropertiesConfig cfg;
//
//	/**
//	 * 
//	 * 取得配置
//	 * 
//	 * @return
//	 */
//	static public void init() {
//
//		if (isInit) {
//			return;
//		}
//		String cfgUrl = CONFIG_PATH;
//		cfg = new PropertiesConfig(cfgUrl);
//
//		String gatewayList = cfg.getValString("GatewayList");
//
//		// 读取网关列表
//		String[] list = gatewayList.split("#");
//		gatewayAddressList = new ArrayList<>();
//		for (String item : list) {
//			System.out.println("gateway list : " + item);
//			String[] v = item.split(":");
//			SocketAddress address = new SocketAddress(v[0], Integer.valueOf(v[1]));
//			gatewayAddressList.add(address);
//		}
//
//		cfg.getValString("ServiceAccepterClassName");
//		cfg.getValString("ClientAccepterClassName");
//		isInit = true;
//
//	}
//
//	static private FoxGateway gateway = null;
//
//	/**
//	 * 打开本机网关
//	 */
//	static public void openLocalGateway() {
//
//		if (gateway != null) {
//			//logger.info("网关已创建 {} {}", gateway.getIp(), gateway.getPort1());
//			return;
//		}
//
//		init();
//		
//		//TODO 服务器多ip情况需要完善
//		List<String> ipList = ServerUtils.getLocalIPList();
//		
//		String localIp = ServerUtils.getLocalIp();
//
//
//		// 网关端口， 先从配置中找，如果找不到，则默认16767
//		int port = 16767;
//		//内部通讯
//		int port2 = port - 100;
//		
//		for (SocketAddress address : gatewayAddressList) {
//			if (localIp.equals(address.getHost())) {
//				port = address.getPort();
//				port2 = port - 100;
//			}
//		}
//		
//		gateway = new FoxGateway(port, port2);
//		
//
//		getLogger().info(String.format("新创建网关AA {%s} socket client {%s}  内部{%s}", localIp, port , port2));
//		// 检测是否有网关服务器，如无，则创建;
//		boolean hasServer = ServerUtils.checkHasSocketServer(localIp, port);
//		if (!hasServer) {
//
//			getLogger().info(String.format("新创建网关 {%s} {%s}", localIp, port));
//		} else {
//			getLogger().info(String.format("网关已创建 {%s} {%s}", localIp, port));
//		}
//		
//		int wsPort = port - 1;
//		new FoxWsGateway(wsPort);
//		getLogger().info(String.format("新创建网关 websocket port ", localIp, wsPort));
//
//	}
//
//	/**
//	 * 创建一个客户端，并握手
//	 */
//	static public FoxTcpClient createClient(String host, int port, IFoxAccepter<InnerMessage> accepter) {
//		init();
//		FoxTcpClient client = new FoxTcpClient(host, port, accepter);
//		return client;
//	}
//	
//	/**
//	 * 创建一个机器人，并握手
//	 */
//	static public FoxTcpClient createRobot(String host, int port, IRobot<InnerMessage> accepter) {
//		init();
//		FoxTcpClient client = new FoxTcpClient(host, port, accepter);
//		accepter.setTcpClient(client);
//		return client;
//	}
//
//	/**
//	 * 创建一个消息服务器，并握手
//	 */
//	static public SubService createMQService(IFoxAccepter<InnerMessage> accepter) {
//
//		init();
//		SubService mqc = new SubService(accepter);
//		return mqc;
//
//	}
//
//
//}
