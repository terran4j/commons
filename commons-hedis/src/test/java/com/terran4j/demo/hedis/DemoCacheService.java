package com.terran4j.demo.hedis;

import com.terran4j.commons.hedis.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DemoCacheService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoCacheService.class);

    @Autowired
    private CacheService cacheService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("演示如何使用 CacheService 服务");

        //  向缓存写入对象。
        String key = "u1";
        User user = new User(1, "neo", new Date());
        cacheService.setObject(key, user, null);

        // 从 缓存中读取对象。
        User value = cacheService.getObject(key, User.class);
        log.info("cache key = {}, value = {}", key, value);
    }

}
