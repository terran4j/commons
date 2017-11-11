package com.terran4j.commons.rediscow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terran4j.commons.rediscow.cache.CacheService;
import com.terran4j.commons.rediscow.cache.RedisTemplateCacheService;
import com.terran4j.commons.rediscow.dschedule.DSchedulingAspect;
import com.terran4j.commons.rediscow.dsyn.DSynchronizedAspect;
import com.terran4j.commons.util.Strings;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisCowConfiguration {

	private static final Logger log = LoggerFactory.getLogger(RedisCowConfiguration.class);

	@Value("${spring.redis.host:127.0.0.1}")
	private String host;

	@Value("${spring.redis.port:6379}")
	private int port;

	@Value("${spring.redis.pool.max-idle:8}")
	private int maxIdle;

	@Value("${spring.redis.pool.min-idle:0}")
	private int minIdle;

	@Value("${spring.redis.pool.max-active:8}")
	private int maxActive;

	@Value("${spring.redis.pool.max-wait:-1}")
	private long maxWait;

	@Value("${spring.redis.defaultExpiration:30}")
	private int defaultExpiration;
	
	@Bean 
	public CacheService cacheService(RedisTemplate<String, String> redisTemplate) {
		return new RedisTemplateCacheService(redisTemplate);
	}

	@Bean
	public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
		RedisCacheManager manager = new RedisCacheManager(redisTemplate);
		manager.setDefaultExpiration(defaultExpiration); // 设置默认过期时间
		return manager;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {

		// 配置Redis连接池
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxTotal(maxActive);
		config.setMaxWaitMillis(maxWait);
		
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(host);
		factory.setPort(port);
		factory.setPoolConfig(config);
		if (log.isInfoEnabled()) {
			log.info("Jedis config done:\n{}", Strings.toString(config));
		}
		return factory;
	}

	@Bean
	public Jedis jedis(RedisConnectionFactory factory) {
		JedisConnection jedisConnection = (JedisConnection) factory.getConnection();
		Jedis jedis = jedisConnection.getNativeConnection();
		return jedis;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		Jackson2JsonRedisSerializer<Object> jackson = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson.setObjectMapper(objectMapper);
		template.setValueSerializer(jackson);
		template.afterPropertiesSet();
		return template;
	}

	@Bean
	@ConditionalOnMissingBean(DSchedulingAspect.class)
	public DSchedulingAspect distributedSchedulingAspect() {
		return new DSchedulingAspect();
	}
	
	@Bean
	@ConditionalOnMissingBean(DSynchronizedAspect.class)
	public DSynchronizedAspect distributedSynchronizedAspect() {
		return new DSynchronizedAspect();
	}

}
