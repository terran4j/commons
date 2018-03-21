package com.terran4j.test.hedis.dsyn;

import com.terran4j.test.hedis.RedisTestConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.terran4j.commons.hedis.config.EnableHedis;

@EnableHedis
@Import({ RedisTestConfig.class })
@SpringBootApplication
public class CacheTestApplication {

	@Bean
	public HomeService homeService() {
		return new HomeService();
	}

}