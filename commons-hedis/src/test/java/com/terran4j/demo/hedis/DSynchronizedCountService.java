package com.terran4j.demo.hedis;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class DSynchronizedCountService implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DSynchronizedCountService.class);

	@Autowired
	private CountService countService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			int count = countService.doIncrementAndGet("count");
			log.info("\ncount = {}", count);

			int sleepTime = random.nextInt(50);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// ignore.
			}
		}
	}
}
