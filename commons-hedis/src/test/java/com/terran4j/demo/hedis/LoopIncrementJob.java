package com.terran4j.demo.hedis;

import com.terran4j.commons.hedis.dschedule.DScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LoopIncrementJob {

    private static final Logger log = LoggerFactory.getLogger(LoopIncrementJob.class);

    private static final String key = "demo3-scheduling-counter";

    @Autowired
    private CountService countService;

    @DScheduling(lockExpiredSecond = 2)
    @Scheduled(cron = "0/1 * * * * *")
    public void loopIncrement() {
        int count = countService.doIncrementAndGet(key);
        log.info("\nloopIncrement, counter = {}", count);
    }

}