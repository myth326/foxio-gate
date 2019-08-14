package cn.foxio.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * spring 启动
 * @author lucky
 *
 */
@SpringBootApplication
public class AppBoot {
	
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(AppBoot.class, args);
		FoxGatewayBoot fox = context.getBean(FoxGatewayBoot.class);
		fox.start();
	}

}
