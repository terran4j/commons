package com.terran4j.test.hedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

import com.terran4j.commons.hedis.config.EnableHedis;
import com.terran4j.commons.hedis.dschedule.DScheduling;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.test.hedis.dsyn.CountService;

@EnableHedis // 一定要加这个，不然不会启动任务调度。
@SpringBootApplication
public class SchedulingApplication {
	
	private static final Logger log = LoggerFactory.getLogger(SchedulingApplication.class);
	
	@Autowired
	private CountService countService;

	@DScheduling("SchedulingDemo")
	@Scheduled(cron = "0/${service.scheduling.runRate:5} * * * * *")
	public void exe() throws InterruptedException {
		try {
			log.info("\nexe, time = " + countService.incrementAndGet());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(SchedulingApplication.class);
		app.setWebEnvironment(false);
		app.run(args);
	}

}
