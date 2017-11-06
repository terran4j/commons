package com.terran4j.commons.rediscow.dschedule;

public class JobExeInfo {

	private Long beginTime;
	
	private Long endTime;
	
	private String className;
	
	private String methodName;
	
	private int resultCode;
	
	private String message;
	
	private String instanceId;
	
	private boolean running = false;

	/**
	 * @return the beginTime
	 */
	public final Long getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime the beginTime to set
	 */
	public final void setBeginTime(Long beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return the endTime
	 */
	public final Long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public final void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the resultCode
	 */
	public final int getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public final void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public final void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the instanceId
	 */
	public final String getInstanceId() {
		return instanceId;
	}

	/**
	 * @param instanceId the instanceId to set
	 */
	public final void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	/**
	 * @return the className
	 */
	public final String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public final void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the methodName
	 */
	public final String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public final void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the running
	 */
	public final boolean isRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	public final void setRunning(boolean running) {
		this.running = running;
	}
	
}
