package com.terran4j.example.commons.hedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.hedis.EnableRedisCow;

@EnableRedisCow
@SpringBootApplication
public class RedisCowDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RedisCowDemoApplication.class);
		app.setWebEnvironment(false);
		app.run(args);
	}

}
