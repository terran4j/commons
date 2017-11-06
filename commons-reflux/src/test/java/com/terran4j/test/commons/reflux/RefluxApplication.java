package com.terran4j.test.commons.reflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.reflux.EnableReflux;

@EnableReflux
@SpringBootApplication
public class RefluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefluxApplication.class, args);
	}
	
}