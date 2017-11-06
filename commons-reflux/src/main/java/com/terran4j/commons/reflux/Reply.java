package com.terran4j.commons.reflux;

/**
 * 客户端的应答内容。
 * 
 * @author wei.jiang
 */
public class Reply<T> {

	private T data;
	
	private String clientHost;
	
	private String clientIP;
	
	private long requestTime;
	
	private long responseTime;
	
	private long resultCode;
	
	private String resultName;
	
	private String message;

	/**
	 * @return the data
	 */
	public final T getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public final void setData(T data) {
		this.data = data;
	}

	/**
	 * @return the clientHost
	 */
	public final String getClientHost() {
		return clientHost;
	}

	/**
	 * @param clientHost the clientHost to set
	 */
	public final void setClientHost(String clientHost) {
		this.clientHost = clientHost;
	}

	/**
	 * @return the clientIP
	 */
	public final String getClientIP() {
		return clientIP;
	}

	/**
	 * @param clientIP the clientIP to set
	 */
	public final void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	/**
	 * @return the requestTime
	 */
	public final long getRequestTime() {
		return requestTime;
	}

	/**
	 * @param requestTime the requestTime to set
	 */
	public final void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	/**
	 * @return the responseTime
	 */
	public final long getResponseTime() {
		return responseTime;
	}

	/**
	 * @param responseTime the responseTime to set
	 */
	public final void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	/**
	 * @return the resultCode
	 */
	public final long getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public final void setResultCode(long resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @return the resultName
	 */
	public final String getResultName() {
		return resultName;
	}

	/**
	 * @param resultName the resultName to set
	 */
	public final void setResultName(String resultName) {
		this.resultName = resultName;
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
	
}
