package com.terran4j.demo.hedis;

import com.terran4j.commons.hedis.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class DemoCacheService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoCacheService.class);

    @Autowired
    private CacheService cacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("演示如何使用 CacheService 服务");
        String key = "k1";
        Integer value = 123;
        //  向缓存写入对象。
        cacheService.setObject(key, 123, null);
        value = cacheService.getObject(key, Integer.class);
        log.info("cache key = {}, value = {}", key, value);
    }

}
