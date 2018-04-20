package com.terran4j.demo.hedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.hedis.config.EnableHedis;

@EnableHedis
@SpringBootApplication
public class HedisDemoApp {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HedisDemoApp.class);
		app.setWebEnvironment(false);
		app.run(args);
	}

}
