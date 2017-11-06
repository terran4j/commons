package com.terran4j.commons.reflux.server;

public class ClientAppInfo {

	private String appKey;
	
	private int maxConnectionCount = 1;
	
	public int getMaxConnectionCount() {
		return maxConnectionCount;
	}

	public void setMaxConnectionCount(int maxConnectionCount) {
		this.maxConnectionCount = maxConnectionCount;
	}

	public final String getAppKey() {
		return appKey;
	}

	public final void setAppKey(String appKey) {
		this.appKey = appKey;
	}

}
