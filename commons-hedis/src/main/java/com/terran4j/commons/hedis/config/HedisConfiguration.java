package com.terran4j.commons.hedis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
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
import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.hedis.cache.RedisTemplateCacheService;
import com.terran4j.commons.hedis.dschedule.DSchedulingAspect;
import com.terran4j.commons.hedis.dsyn.DSynchronizedAspect;
import com.terran4j.commons.util.Strings;

import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

@Configuration
public class HedisConfiguration {

	private static final Logger log = LoggerFactory.getLogger(HedisConfiguration.class);

	@Value("${spring.redis.host:127.0.0.1}")
	private String host;

	@Value("${spring.redis.port:6379}")
	private int port;

    @Value("${spring.redis.password:}")
    private String password;

	@Value("${spring.redis.pool.max-idle:8}")
	private int maxIdle;

	@Value("${spring.redis.pool.min-idle:0}")
	private int minIdle;

	@Value("${spring.redis.pool.max-total:8}")
	private int maxTotal;

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
		config.setMaxTotal(maxTotal);
		config.setMaxWaitMillis(maxWait);
		
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(host);
		factory.setPort(port);
		if (StringUtils.hasText(password)) {
            factory.setPassword(password.trim());
        }
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

    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://" + host + ":" + port);
        if (StringUtils.hasText(password)) {
            serverConfig.setPassword(password);
        }
        serverConfig.setConnectionPoolSize(maxTotal);
        serverConfig.setConnectionMinimumIdleSize(minIdle);

        if (log.isInfoEnabled()) {
            log.info("Redisson config done:\n{}", Strings.toString(config));
        }
        return Redisson.create(config);
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
