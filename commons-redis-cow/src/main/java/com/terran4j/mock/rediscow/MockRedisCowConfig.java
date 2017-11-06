package com.terran4j.mock.rediscow;

import com.terran4j.commons.rediscow.cache.CacheService;
import com.terran4j.commons.util.error.BusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockRedisCowConfig {

    public static final MockCacheService cacheService = new MockCacheService();

    @Bean
    public CacheService cacheService() throws BusinessException {
        return cacheService;
    }

}
