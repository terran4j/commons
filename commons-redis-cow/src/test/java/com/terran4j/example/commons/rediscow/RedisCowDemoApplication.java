package com.terran4j.example.commons.rediscow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.rediscow.EnableRedisCow;

@EnableRedisCow
@SpringBootApplication
public class RedisCowDemoApplication {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RedisCowDemoApplication.class);
		app.setWebEnvironment(false);
		app.run(args);
	}

}
