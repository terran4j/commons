package com.terran4j.test.hedis;

import com.terran4j.test.hedis.dsyn.BaseCacheTest;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class RedissonClientTest extends BaseCacheTest {

    private static final Logger log = LoggerFactory.getLogger(RedissonClientTest.class);

    @Autowired
    private RedissonClient redissonClient;

    private int count = 0;

    private int increment(int i) {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            // ignore.
        }
        return i + 1;
    }

    /**
     * 测试 redissonClient 提供的分布式锁功能。
     */
    @Test
    public void testLock() {
        RLock lock = redissonClient.getLock("my-lock");
        count = 0;
        final int threadCount = 5;
        final int loopCount = 10;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < loopCount; j++) {
                    lock.lock(1, TimeUnit.SECONDS);
                    count = increment(count);
                    lock.unlock();
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                Assert.fail("InterruptedException: " + e.getMessage());
            }
        }
        Assert.assertEquals(threadCount * loopCount, count);
    }

}