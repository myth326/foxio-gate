package cn.foxio.simple.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.foxio.gate.def.GatewayDef;
import cn.foxio.gate.helper.SenderHelper;
import cn.foxio.gate.tcp.SubService;

/**
 * 登陆服务器
 * @author lucky
 *
 */

@Component
public class LoginServer {

	//全部的网关国庆列表
	@Value("${gate.socket.inside.list}")
	private String gatewayList;

	public LoginServer() {
	}
	
	
	
	public void init()
	{
		// 创建一个帐号服务器,并订阅 login 消息
		SubService loginService = new SubService(new LoginMaster() , gatewayList);
		loginService.addSubscribe(3);
		loginService.addSubscribe(GatewayDef.CLIENT_OFF_LINE);
		SenderHelper.register(cn.foxio.simple.config.ServerName.LOGIN_KEY, loginService);
	}


}
