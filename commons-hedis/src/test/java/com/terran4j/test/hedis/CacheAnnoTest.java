package com.terran4j.test.hedis;

import org.junit.Assert;
import org.junit.Test;

import com.terran4j.test.hedis.dsyn.BaseCacheTest;
import com.terran4j.test.hedis.dsyn.Home;
import com.terran4j.test.hedis.dsyn.HomeService;

public class CacheAnnoTest extends BaseCacheTest {
	
	@Test
	public void testCacheable() {
		
		homeService.create(key);
		
		Home home = homeService.get(key);
		Assert.assertNotNull(home);
		Assert.assertEquals(1, homeService.getActionTime(HomeService.GET));
		
		// 由于 get 方法加了 @Cacheable 注解，后续的请求结果都从缓存中取，而不是直接调用方法。
		for (int i = 0; i < 10; i++) {
			home = homeService.get(key);
			Assert.assertNotNull(home);
			Assert.assertEquals(1, homeService.getActionTime(HomeService.GET));
		}
	}
	
	@Test
	public void testCachePut() {
		homeService.create(key);
		
		Home home = homeService.get(key);
		Assert.assertNotNull(home);
		Assert.assertEquals(1, homeService.getActionTime(HomeService.GET));
		
		// 由于 get 方法加了 @Cacheable 注解，后续的请求结果都从缓存中取，而不是直接调用方法。
		homeService.get(key);
		Assert.assertEquals(1, homeService.getActionTime(HomeService.GET));
		
		// 由于 update 方法加了 @CachePut 注解，会更新缓存数据。
		homeService.update(key, 5);
		home = homeService.get(key);
		Assert.assertEquals(5, home.getMemberCount());
		Assert.assertEquals(1, homeService.getActionTime(HomeService.GET));
	}
	
	@Test
	public void testCacheEvict() {
		homeService.create(key);
		Home home = homeService.get(key);
		Assert.assertNotNull(home);
		Assert.assertEquals(1, homeService.getActionTime(HomeService.GET));
		
		homeService.delete(key);
		homeService.create(key);
		home = homeService.get(key);
		Assert.assertNotNull(home);
		Assert.assertEquals(2, homeService.getActionTime(HomeService.GET));
	}
	
}
