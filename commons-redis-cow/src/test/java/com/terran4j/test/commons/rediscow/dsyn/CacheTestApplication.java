package com.terran4j.test.commons.rediscow.dsyn;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.terran4j.commons.rediscow.EnableRedisCow;
import com.terran4j.test.commons.rediscow.RedisTestConfig;

@EnableRedisCow
@Import({ RedisTestConfig.class })
@SpringBootApplication
public class CacheTestApplication {

	@Bean
	public HomeService homeService() {
		return new HomeService();
	}

}