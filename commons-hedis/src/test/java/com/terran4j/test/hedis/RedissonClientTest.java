package com.terran4j.test.hedis;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.test.hedis.dsyn.BaseCacheTest;
import com.terran4j.test.hedis.dsyn.Home;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RedissonClientTest extends BaseCacheTest {
	
	private static final Logger log = LoggerFactory.getLogger(RedissonClientTest.class);

	@Autowired
    private RedissonClient redissonClient;

	private int count = 0;

	@Test
	public void testLock() {
        RLock lock = redissonClient.getLock("my-lock");
        lock.lock(3, TimeUnit.SECONDS);
        count++;
        lock.unlock();
	}


}