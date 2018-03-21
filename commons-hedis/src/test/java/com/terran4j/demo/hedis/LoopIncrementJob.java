package com.terran4j.demo.hedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.terran4j.commons.hedis.dschedule.DScheduling;

@Service
public class LoopIncrementJob {

	private static final Logger log = LoggerFactory.getLogger(LoopIncrementJob.class);

	private static final String key = "demo3-scheduling-counter";

	@Autowired
	private CountService countService;

	@DScheduling("demo3-scheduling-lock")
	@Scheduled(cron = "0/${demo3.scheduling.runRate:5} * * * * *")
	public void loopIncrement() {
		int count = countService.incrementAndGet(key);
		log.info("\nloopIncrement, counter = {}", count);
	}

}
