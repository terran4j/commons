package com.terran4j.commons.hi;

public interface HttpClientListener {

	void beforeExecute(HttpRequest request);
	
	String afterExecute(HttpRequest request, String response);
}
