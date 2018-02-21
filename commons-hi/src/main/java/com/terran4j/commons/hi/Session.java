package com.terran4j.commons.hi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

public final class Session {

	private final HttpClient httpClient;

	public HttpClient getHttpClient() {
		return httpClient;
	}

	private final ApplicationContext applicationContext;
	
	private Map<String, String> params = new HashMap<String, String>();

	private Map<String, String> headers = new HashMap<String, String>();
	
	private final Map<String, String> attrs = new HashMap<String, String>();
	
	private Map<String, String> locals = new HashMap<String, String>();

	Session(HttpClient service, ApplicationContext context) {
		super();
		this.httpClient = service;
		this.applicationContext = context;
		this.params = service.cloneParams();
		this.headers = service.cloneHeaders();
		this.locals = service.cloneLocals();
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	public Map<String, String> getLocals() {
		return locals;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Request action(String action) throws HttpException {
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

	public void setAttribute(String key, String value) {
		this.attrs.put(key, value);
	}
	
	public Session local(String key, String value) {
		this.locals.put(key, value);
		return this;
	}
}