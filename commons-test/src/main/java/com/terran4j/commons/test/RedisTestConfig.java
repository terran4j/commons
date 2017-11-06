package com.terran4j.commons.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:com/terran4j/commons/test/redis.properties")
public class RedisTestConfig {

}
