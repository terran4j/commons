package com.terran4j.commons.util.task;

import lombok.extern.slf4j.Slf4j;

/**
 * 搭建一个永续循环的任务框架，帮助控制循环的节奏、定期打印日志输出报告任务执行情况。
 * @author wei.jiang
 *
 */
@Slf4j
public abstract class LoopExecuteTask implements Runnable {

	private long sleepTime = 1000;

	private long reportIntervalSecond = 60;

	private long executeCount;

	private long failedCount;

	private Thread thread;

	private volatile boolean running = false;

	public LoopExecuteTask() {
		this(0);
	}

	public LoopExecuteTask(long sleepTime) {
		super();
		this.sleepTime = sleepTime;
	}

	public final long getSleepTime() {
		return sleepTime;
	}

	public long getReportIntervalSecond() {
		return reportIntervalSecond;
	}

	public long getFailedCount() {
		return failedCount;
	}

	public LoopExecuteTask setReportIntervalSecond(long reportIntervalSecond) {
		this.reportIntervalSecond = reportIntervalSecond;
		return this;
	}

	public long getExecuteCount() {
		return executeCount;
	}

	public Thread getThread() {
		return thread;
	}

	public boolean isRunning() {
		return running;
	}

	public final LoopExecuteTask setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
		return this;
	}

	@Override
	public final void run() {
		if (this.thread != null) {
			throw new IllegalStateException("LoopExecuteTask Object can't be running in multi-thread.");
		}

		onStart();
		
		thread = Thread.currentThread();
		String threadName = thread.getName();
		if (log.isInfoEnabled()) {
			log.info("{} is starting...", threadName);
		}

		executeCount = 0;
		failedCount = 0;
		long totalSpendTime = 0;
		long lastReportTime = System.currentTimeMillis();
		running = true;
		long retrySleepTime = 100;
		while (!thread.isInterrupted()) {
			executeCount++;
			try {
				long t0 = System.currentTimeMillis();
				boolean willContinue = execute();
				long t = System.currentTimeMillis() - t0;
				totalSpendTime += t;
				if (!willContinue) {
					sleep();
				}
				retrySleepTime = 100;
			} catch (Exception e) {
				failedCount++;
				boolean willContinue = handle(e);
				if (!willContinue) {
					break;
				} else { // 失败后的重试，间隔时间应该是递增的。
					retrySleepTime = retrySleepTime * 2;
					if (retrySleepTime > sleepTime) {
						retrySleepTime = sleepTime;
					}
					sleep(retrySleepTime);
				}
			}

			long t = System.currentTimeMillis() - lastReportTime;
			if (t > reportIntervalSecond * 1000) {
				if (log.isInfoEnabled()) {
					log.info(
							"{} is running..., executeCount = {}, failedCount = {}, totalSpendTime = {}, avgSpendTime = {}",
							threadName, executeCount, failedCount, totalSpendTime, totalSpendTime / executeCount);
				}
				lastReportTime = System.currentTimeMillis();
			}
		}
		
		running = false;
		if (log.isInfoEnabled()) {
			log.info("{} is stopped...", threadName);
		}
		thread = null;
		
		onStop();
	}

	protected final void sleep() {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			if (log.isWarnEnabled()) {
				log.warn("{} is Interrupted.", thread.getName());
			}
		}
	}
	
	protected final void sleep(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			if (log.isWarnEnabled()) {
				log.warn("{} is Interrupted.", thread.getName());
			}
		}
	}

	protected boolean handle(Exception e) {
		String threadName = thread.getName();
		log.error("{} execute occur Exception[{}], cause by {}", threadName, e.getClass().getName(), e.getMessage(), e);
		return true;
	}

	/**
	 * 执行操作（一次循环里面的内容）。
	 * @return 是否继续循环执行， true表示继续下一次的循环执行，
	 * false表示sleep一段时间再进入下一次的循环执行
	 * @throws Exception 执行出错。
	 */
	protected abstract boolean execute() throws Exception;
	
	protected void onStart() {
	}
	
	protected void onStop() {
	}

}