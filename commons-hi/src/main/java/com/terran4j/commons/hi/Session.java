package com.terran4j.commons.hi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

public final class Session {

	private final HttpClient httpClient;

	private final ApplicationContext applicationContext;
	
//	private Map<String, String> params = new HashMap<>();
//
//	private Map<String, String> headers = new HashMap<>();
//
//	private final Map<String, String> attrs = new HashMap<>();
	
	private Map<String, String> locals = new HashMap<>();

	Session(HttpClient service, ApplicationContext context) {
		super();
		this.httpClient = service;
		this.applicationContext = context;
        this.locals = service.cloneLocals();
//		this.params = service.cloneParams();
//		this.headers = service.cloneHeaders();
	}
//
//	public Map<String, String> getActualParams() {
//		return params;
//	}

    public HttpClient getHttpClient() {
        return httpClient;
    }
	
	public Map<String, String> getLocals() {
		return locals;
	}

//	public Map<String, String> getHeaders() {
//		return headers;
//	}

	public Request createRequest(String action) throws HttpException {
		Action actionObject = httpClient.getActions().get(action);
		if (actionObject == null) {
			throw new HttpException(HttpErrorCode.ACTION_NOT_FOUND)
					.put("action", action).as(HttpException.class);
		}
		Request request = new Request(actionObject, this, applicationContext);
		return request;
	}
	
	public HttpClient getService() {
		return httpClient;
	}
	
	public Session local(String key, String value) {
		this.locals.put(key, value);
		return this;
	}

	public String local(String key) {
	    return this.locals.get(key);
    }
}