package com.terran4j.demo.hedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.hedis.dsyn.DSynchronized;
import com.terran4j.commons.util.error.BusinessException;

@Service
public class CountService {

	@Value("${demo3.scheduling.sleep:1000}")
	private long sleepTime;

	@Autowired
	private CacheService cacheService;

	/**
	 * 一个没有并发控制的递增计算，需要调用方避免并发访问。
	 */
	public int incrementAndGet(String key) {
		Integer value = null;
		try {
			value = cacheService.getObject(key, Integer.class);
			if (value == null) {
				value = 0;
			}
		} catch (BusinessException e1) {
			throw new RuntimeException(e1);
		}

		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// ignore.
		}

		value++;
		cacheService.setObject(key, value, null);
		return value;
	}
	
	/**
	 * 对 incrementAndGet 方法加上分布式并发控制。
	 */
	@DSynchronized("'demo3-dsyn-' + #key")
	public int dsynIncrementAndGet(@Param("key") String key) {
		return incrementAndGet(key);
	}

}
