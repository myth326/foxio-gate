package cn.foxio.gate.tcp;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.foxio.gate.face.IFoxAccepter;
import cn.foxio.gate.tcp.data.InnerMessage;
import cn.foxio.gate.tcp.data.SocketAddress;
import cn.foxio.gate.tcp.service.FoxTcpService;
import cn.foxio.gate.tools.CronJobManager;
import cn.foxio.gate.tools.CronJobManager.CountDownTask;

/**
 * 子服务器
 * 
 * @author lucky
 *
 */
public class SubService extends SubServiceBase {

	private ArrayList<SocketAddress> addresList = new ArrayList<>();
	
	private IFoxAccepter<InnerMessage> accepter;
	
	//全部的网关国庆列表
	//@Value("${gate.socket.inside.list}")
	private String gatewayList;



	public SubService(IFoxAccepter<InnerMessage> accepter , String gatewayList) {

		this.accepter = accepter;
		this.gatewayList = gatewayList;
		reloadGatewayList(null);
	}
	

	private void addTcpClient(SocketAddress address , IFoxAccepter<InnerMessage> accepter) {
		
		this.accepter = accepter;
		FoxTcpService client = new FoxTcpService(address.getHost(), address.getPort(), accepter , this);
		clientList.add(client);
	}

	private CountDownTask current;

	private void addTask() {
		if (current != null) {
			return;
		}
		
		Consumer<Object> call = obj -> {
			reloadGatewayList(obj);
		};

		CountDownTask task = new CountDownTask((long) 1000 * 30, new Object(), call);
		CronJobManager.getInstance().addCountDownJob(task.getId() + "mqService", "reloadGatewayList" + new Random().nextInt(99999) , task);

		current = task;
	}
	

	/**
	 * 载入
	 * 
	 * @param obj
	 */
	private void reloadGatewayList(Object obj) {

		// 读取网关列表
		String[] list = gatewayList.split("#");
		for (String item : list) {
			String[] v = item.split(":");
			SocketAddress address = new SocketAddress(v[0], Integer.valueOf(v[1]));
			address.setPort(address.getPort() -100 );

			if (!checkInAddresList(address)) {
				// 有加入新的路由;
				addresList.add(address);
				addTcpClient(address , accepter);
			}

		}

		current = null;
		addTask();
	}

	private boolean checkInAddresList(SocketAddress address) {
		
		if (addresList == null) {
			return false;
		}
		for (SocketAddress a : addresList) {
			if (a.getHost() != null && a.getHost().equals(address.getHost()) && a.getPort() == address.getPort()) {
				return true;
			}
		}
		return false;
	}

}
