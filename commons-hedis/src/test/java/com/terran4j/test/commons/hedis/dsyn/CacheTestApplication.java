package com.terran4j.test.commons.hedis.dsyn;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.terran4j.commons.hedis.EnableRedisCow;
import com.terran4j.test.commons.hedis.RedisTestConfig;

@EnableRedisCow
@Import({ RedisTestConfig.class })
@SpringBootApplication
public class CacheTestApplication {

	@Bean
	public HomeService homeService() {
		return new HomeService();
	}

}