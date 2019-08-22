package cn.foxio.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import cn.foxio.simple.gate.FoxGatewayBoot;
import cn.foxio.simple.login.LoginServer;

/**
 * 示例APP启动类
 * @author lucky
 *
 */
@SpringBootApplication
public class SimpleAppBoot {
	
	public static void main(String[] args) throws Exception {
		
		//启动spring
		ConfigurableApplicationContext context = SpringApplication.run(SimpleAppBoot.class, args);
		
		
		//启动网关
		FoxGatewayBoot fox = context.getBean(FoxGatewayBoot.class);
		fox.start();
		
		//启动登陆服 [此示例在一个进程中，建议分开布署]
		LoginServer login = context.getBean(LoginServer.class);
		login.init();
	}

}
