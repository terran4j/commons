package com.terran4j.commons.httpinvoker;

public interface HttpClientListener {

	void beforeExecute(HttpRequest request);
	
	String afterExecute(HttpRequest request, String reponse);
}
