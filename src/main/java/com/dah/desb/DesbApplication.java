package com.dah.desb;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = { "com.dah" })
public class DesbApplication {

	public static void main(String[] args) {
		final ApplicationContext context = SpringApplication.run(DesbApplication.class, args);
		final CamelSpringBootApplicationController controller = context.getBean(CamelSpringBootApplicationController.class);
		controller.run();
	}

}
