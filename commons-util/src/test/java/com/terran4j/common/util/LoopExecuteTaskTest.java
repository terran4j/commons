package com.terran4j.common.util;

import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terran4j.commons.util.task.LoopExecuteTask;

@RunWith(SpringJUnit4ClassRunner.class)
public class LoopExecuteTaskTest {

	private static final Logger log = LoggerFactory.getLogger(LoopExecuteTaskTest.class);

	@Test
	public void testInterrupt() {
		log.info("testInterrupt");
		final CountDownLatch latch = new CountDownLatch(1);
		LoopExecuteTask task = new LoopExecuteTask(){

			@Override
			protected boolean execute() throws Exception {
				if (latch.getCount() == 1) {
					latch.countDown();
				}
				return false;
			}
			
		};
		Thread thread = new Thread(task);
		
		thread.start();
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		Assert.assertTrue(task.isRunning());
		
//		thread.interrupt();
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//		}
//		Assert.assertFalse(task.isRunning());
	}
	
}
