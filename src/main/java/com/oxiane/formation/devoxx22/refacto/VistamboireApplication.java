package com.oxiane.formation.devoxx22.refacto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.oxiane.formation.devoxx22.refacto")
@EnableAutoConfiguration
public class VistamboireApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(VistamboireApplication.class);
		ConfigurableApplicationContext ctx = application.run(args);
	}

}
