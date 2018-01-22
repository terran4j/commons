package com.terran4j.test.commons.hedis.dsyn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.util.error.BusinessException;

@Service
public class CountService {
	
	private static final String key = "test_scheduling_counter";
	
	@Autowired
	private CacheService cacheService;
	
	/**
	 * 一个没有并发控制的递增计算，需要调用方避免并发访问。
	 * @throws BusinessException
	 */
	public int incrementAndGet() throws BusinessException {
		Integer value = cacheService.getObject(key, Integer.class);
		if (value == null) {
			value = 0;
		}
		value++;
		cacheService.setObject(key, value, null);
		return value;
	}
}
