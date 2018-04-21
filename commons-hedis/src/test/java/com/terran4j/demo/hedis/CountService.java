package com.terran4j.demo.hedis;

import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.hedis.dsyn.DSynchronized;
import com.terran4j.commons.util.error.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Service
public class CountService {

    @Autowired
    private CacheService cacheService;

    /**
     * 一个没有并发控制的递增计算，需要调用方避免并发访问。
     */
    public int doIncrementAndGet(String key) {
        // 从 Redis 缓存中取出计数器变量：
        Integer counter;
        try {
            counter = cacheService.getObject(key, Integer.class);
            if (counter == null) {
                counter = 0;
            }
        } catch (BusinessException e1) {
            throw new RuntimeException(e1);
        }

        // 故意让线程休眠一段时间，让并发问题更严重。
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore.
        }

        // 在本地让计数器变量加 1：
        counter++;

        // 将变量写回 Redis 缓存：
        cacheService.setObject(key, counter, null);
        return counter;
    }

    /**
     * 对 incrementAndGet 方法加上分布式并发控制。
     */
    @DSynchronized("'incrementAndGet-' + #key")
    public int incrementAndGet(@Param("key") String key) {
        return doIncrementAndGet(key);
    }

}