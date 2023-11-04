package com.terran4j.commons.hedis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.hedis.cache.RedisTemplateCacheService;
import com.terran4j.commons.hedis.dschedule.DSchedulingAspect;
import com.terran4j.commons.hedis.dsyn.DSynchronizedAspect;
import com.terran4j.commons.util.Strings;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
public class HedisConfiguration {

    @Value("${spring.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.pool.max-total:8}")
    private int maxTotal;

    @Value("${spring.redis.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.redis.pool.min-idle:0}")
    private int minIdle;

    /**
     * 获取连接时的最大等待时间，默认为 -1 表示永久等待。
     */
    @Value("${spring.redis.pool.max-wait:-1}")
    private long maxWait;

    @Value("${spring.redis.message.server:}")
    private String serverId;

    @Value("${spring.redis.message.listener:}")
    private String messageListenerClass;


    /**
     * 对于缓存管理器，设置默认的过期时间。
     */
    @Value("${spring.redis.cache.defaultExpirationSecond:30}")
    private int defaultExpiration;

    @Bean
    public CacheService cacheService(RedisTemplate<String, String> redisTemplate) {
        return new RedisTemplateCacheService(redisTemplate);
    }

//    @Bean
//    public MessageListener getMessageLister(){
//        if(Strings.isNull(this.messageListenerClass))return null;
//        try {
//            return (MessageListener) HedisConfiguration.class.getClassLoader().loadClass(this.messageListenerClass).newInstance();
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory redisConnectionFactory, ApplicationContext context) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // 监听所有库的key过期事件
        container.setConnectionFactory(redisConnectionFactory);
        // 所有的订阅消息，都需要在这里进行注册绑定,new PatternTopic(TOPIC_NAME1)表示发布的主题信息
        // 可以添加多个 messageListener，配置不同的通道
        if(!Strings.isNull(this.serverId) && !Strings.isNull(this.messageListenerClass)) {
            try {
                MessageListener listener = (MessageListener) context.getBean(HedisConfiguration.class.getClassLoader().loadClass(this.messageListenerClass));
                log.info("rediscontainer.registerlisten {} {}", this.serverId, listener);
                container.addMessageListener(listener, new PatternTopic(this.serverId));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
//            MessageListener listener = this.getMessageLister();
        }
////        container.addMessageListener(adapter, new PatternTopic(TOPIC_NAME2));
        /**
         * 设置序列化对象
         * 特别注意：1. 发布的时候需要设置序列化；订阅方也需要设置序列化
         *         2. 设置序列化对象必须放在[加入消息监听器]这一步后面，否则会导致接收器接收不到消息
         */
        Jackson2JsonRedisSerializer seria = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        seria.setObjectMapper(objectMapper);
        container.setTopicSerializer(seria);

        return container;
    }

//    @Bean
//    public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
//        RedisCacheManager manager = new RedisCacheManager(redisTemplate);
//        manager.setDefaultExpiration(defaultExpiration); // 设置默认过期时间
//        return manager;
//    }
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Duration expiration = Duration.ofSeconds(defaultExpiration);
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(expiration)).build();
    }
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        // 配置Redis连接池
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);

        // 最大空闲数，不能大于连接池总大小。
        if (maxIdle > maxTotal) {
            maxIdle = maxTotal;
        }
        config.setMaxIdle(maxIdle);

        // 最小空闲数，不能大于最大空闲数的一半。
        if (minIdle < 0) {
            minIdle = 0;
        }
        int maxMinIdle = (maxIdle + 1) / 2;
        if (minIdle > maxMinIdle) {
            minIdle = maxMinIdle;
        }
        config.setMinIdle(minIdle);

        config.setMaxWaitMillis(maxWait);

        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        if (StringUtils.hasText(password)) {
            factory.setPassword(password.trim());
        }
        factory.setPoolConfig(config);
        if (log.isInfoEnabled()) {
            log.info("Jedis config done: \n{}", Strings.toString(config));
        }
        return factory;
    }

    @Bean
    public Jedis jedis(RedisConnectionFactory factory) {
        JedisConnection jedisConnection = (JedisConnection) factory.getConnection();
        Jedis jedis = jedisConnection.getNativeConnection();
        return jedis;
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://" + host + ":" + port);
        if (StringUtils.hasText(password)) {
            serverConfig.setPassword(password);
        }
        serverConfig.setConnectionPoolSize(maxTotal);
        serverConfig.setConnectionMinimumIdleSize(minIdle);

        if (log.isInfoEnabled()) {
            log.info("Redisson config done: \n{}", Strings.toString(config));
        }
        return Redisson.create(config);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        Jackson2JsonRedisSerializer<Object> jackson = new Jackson2JsonRedisSerializer<>(Object.class);
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
