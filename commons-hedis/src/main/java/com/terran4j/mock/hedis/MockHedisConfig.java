package com.terran4j.mock.hedis;

import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.util.error.BusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockHedisConfig {

    public static final MockCacheService cacheService = new MockCacheService();

    @Bean
    public CacheService cacheService() throws BusinessException {
        return cacheService;
    }

}
