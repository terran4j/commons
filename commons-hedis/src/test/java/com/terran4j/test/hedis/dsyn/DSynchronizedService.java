package com.terran4j.test.hedis.dsyn;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.terran4j.commons.hedis.cache.CacheService;
import com.terran4j.commons.hedis.dsyn.DSynchronized;
import com.terran4j.commons.util.error.BusinessException;

@Service
public class DSynchronizedService {

	private static final Random random = new Random();

	@Autowired
	private CacheService cacheService;

	/**
	 * 一个需要并发控制的“++”计算。
	 * 
	 * @param name
	 * @param spendTime1
	 * @param spendTime2
	 * @return
	 * @throws BusinessException
	 */
	@DSynchronized(value = "'increment_' + #name")
	public int incrementAndGet(@Param("name") String name, long spendTime1, long spendTime2) throws BusinessException {
		Integer current = cacheService.getObject(name, Integer.class);
		if (current == null) {
			current = new Integer(0);
			cacheService.setObject(name, current, null);
		}

		sleep(spendTime1);

		int result = current + 1;
		cacheService.setObject(name, new Integer(result), null);

		sleep(spendTime2);

		return getValue(name);
	}

	public int getValue(String name) throws BusinessException {
		Integer result = cacheService.getObject(name, Integer.class);
		return result == null ? 0 : result;
	}

	private void sleep(long sleepTime) {
		if (sleepTime < 0) {
			int max = 0 - (int) sleepTime;
			int actualSleepTime = random.nextInt(max);
			try {
				Thread.sleep(actualSleepTime);
			} catch (InterruptedException e) {
			}
		} else if (sleepTime > 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
	}

}
