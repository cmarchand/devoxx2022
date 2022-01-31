package com.oxiane.formation.devoxx22.refacto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.oxiane.formation.devoxx22.refacto")

public class VistamboireApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(VistamboireApplication.class, args);
		System.out.println(ctx.getBean("information", String.class));
	}

}
