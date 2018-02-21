package com.terran4j.commons.hi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HiConfiguration {

	@Bean
	public HttpCommand httpCommand() {
		return new HttpCommand();
	}
	
}
