package com.terran4j.test.commons.hedis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.test.commons.hedis.dsyn.BaseCacheTest;
import com.terran4j.test.commons.hedis.dsyn.Home;

public class JedisCacheServiceTest extends BaseCacheTest {
	
	private static final Logger log = LoggerFactory.getLogger(JedisCacheServiceTest.class);

	private Map<String, Home> createHome() {
		Map<String, Home> map = new HashMap<String, Home>();
		Home baba = new Home();
		baba.setName("ma");
		map.put("baba", baba);
		Home mama = new Home();
		mama.setName("li");
		map.put("mama", mama);
		Home girl = new Home();
		girl.setName("yu");
		map.put("girl", girl);
		return map;
	}

	@Test
	public void testExistedAndRemove() {
		cacheService.setObject("home", "testtest", null);
		Assert.assertEquals(true, cacheService.existed("home"));
		cacheService.remove("home");
		Assert.assertEquals(false, cacheService.existed("home"));
	}

	@Test
	public void testSetAndGetObject() throws BusinessException {
		Home home = new Home("terran4j");
		cacheService.setObject("home1", home, null);
		Assert.assertEquals(true, cacheService.existed("home1"));
		Home homeRedis = cacheService.getObject("home1", Home.class);
		Assert.assertEquals(home, homeRedis);
	}

	@Test
	public void testGetAndSetHashEntry() throws BusinessException {
		Home one = new Home("one");
		cacheService.setHashEntry("home2", "one", one, null);
		Assert.assertEquals(true, cacheService.existed("home2"));
		Home oneRedis = cacheService.getHashEntry("home2", "one", Home.class);
		Assert.assertEquals(one, oneRedis);
	}

	@Test
	public void testSetAndGetHashMap() throws BusinessException {
		Map<String, Home> homes = createHome();
		cacheService.setHashMap("home3", homes, Home.class);
		Assert.assertEquals(true, cacheService.existed("home3"));
		Map<String, Home> homesReis = cacheService.getHashMap("home3", Home.class);
		Assert.assertEquals(homes, homesReis);
	}
	
	@Test
	public void testConcurrent() throws Exception {
		final Home one = new Home("one");
		cacheService.setObject("one", one, null);
		
		final int threadCount = 10;
		final int exeCount = 10;
		AtomicBoolean failed = new AtomicBoolean(false);
		Thread[] threads = new Thread[threadCount];
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < threadCount; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < exeCount; j++) {
						try {
							Home obj = cacheService.getObject("one", Home.class);
							Assert.assertEquals(one, obj);
						} catch (Throwable e) {
							e.printStackTrace();
							failed.set(true);
						}
					}
				}
			});
			threads[i].setName("Thread - " + i);
			threads[i].start();
		}
		for (int i = 0; i < threadCount; i++) {
			threads[i].join();
		}
		long spend = System.currentTimeMillis() - t0;
		if (log.isInfoEnabled()) {
			log.info("{} threads, execute {} times per thread, total spend {}ms",
					threadCount, exeCount, spend);
		}
		Assert.assertFalse(failed.get());
		Assert.assertTrue("sepnd much more time: " + spend, spend < 1000);
	}

}