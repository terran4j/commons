package com.terran4j.commons.httpinvoker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpInvokerConfiguration {

	@Bean
	public HttpCommand httpCommand() {
		return new HttpCommand();
	}
	
}
