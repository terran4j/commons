package com.terran4j.test.commons.rediscow.dsyn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 本类模拟了对 Home 实体进行增删查改的操作。<br>
 * 同时演示了如何用 @CachePut, @CacheEvict, @Cacheable 注解来管理缓存数据。
 * 
 * @author wei.jiang
 *
 */
@CacheConfig(cacheNames = "homes")
@Service
public class HomeService {

	public static final String DELETE = "delete";

	public static final String GET = "get";

	public static final String UPDATE = "update";

	public static final String CREATE = "create";

	private Map<String, Home> homes = new ConcurrentHashMap<>();

	private Map<String, AtomicInteger> actionTimes = new ConcurrentHashMap<>();

	public HomeService() {
		clear();
	}

	public synchronized int getActionTime(String action) {
		return actionTimes.get(action).get();
	}

	public void clear() {
		homes = new ConcurrentHashMap<>();
		actionTimes = new ConcurrentHashMap<>();
		actionTimes.put(CREATE, new AtomicInteger(0));
		actionTimes.put(UPDATE, new AtomicInteger(0));
		actionTimes.put(GET, new AtomicInteger(0));
		actionTimes.put(DELETE, new AtomicInteger(0));
	}

	public Home create(String name) {
		actionTimes.get(CREATE).incrementAndGet();
		if (homes.containsKey(name)) {
			throw new RuntimeException("home existed: " + name);
		}
		Home home = new Home(name);
		homes.put(name, home);
		return home;
	}

	/**
	 * <code>@Cacheable</code>: 配置于函数上，能够缓存函数调用的返回值。<br>
	 * 在调用函数之前，会先从缓存中获取，若不存在才真正调用函数，并缓存函数的返回值。<br>
	 * 所以 Cacheable 注解一般用在查询操作上。
	 * 
	 * @param name
	 * @return
	 */
	@Cacheable(key = "'home-' + #name")
	public Home get(String name) {
		actionTimes.get(GET).incrementAndGet();
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return homes.get(name);
	}

	/**
	 * <code>@CachePut</code>: 配置于函数上，能够缓存函数调用的结果。<br>
	 * 它与@Cacheable不同的是，它每次都会先真的调用函数，再缓存函数的返回值。 <br>
	 * 所以 CachePut 主要用于数据新增和修改操作上。
	 * 
	 * @param name
	 * @param memberCount
	 * @return
	 */
	@CachePut(key = "'home-' + #name")
	public Home update(String name, int memberCount) {
		actionTimes.get(UPDATE).incrementAndGet();
		if (!homes.containsKey(name)) {
			throw new RuntimeException("home not exist: " + name);
		}
		Home home = homes.get(name);
		home.setMemberCount(memberCount);
		return home;
	}

	/**
	 * <code>@CacheEvict</code>: 配置于函数上，删除对应的缓存。<br>
	 * 所以 CacheEvict 一般用在删除操作上，用来从缓存中删除相应数据。
	 * 
	 * @param name
	 * @return
	 */
	@CacheEvict(key = "'home-' + #name")
	public Home delete(String name) {
		actionTimes.get(DELETE).incrementAndGet();
		return homes.remove(name);
	}

}
