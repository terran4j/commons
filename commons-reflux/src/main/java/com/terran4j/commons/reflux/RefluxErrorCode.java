package com.terran4j.commons.reflux;

import com.terran4j.commons.util.error.ErrorCode;

public enum RefluxErrorCode implements ErrorCode {
	
	CLIENT_NOT_FOUND(2, "client.not.found"),
	
	CLIENT_AUTH_FAILED(3, "client.auth.failed"),
	
	CLIENT_CONNECTION_FULL(5, "client.connection.full"),
	
	NOT_AUTHED(6, "not.authed"),
	
	CLIENT_ID_USED_CONCURRENTLY(7, "clientId.used.concurrently"),
	
	;
	

	private final int value;
	
	private final String name;

	private RefluxErrorCode(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public final int getValue() {
		return value;
	}
	
	public final String getName() {
		return name;
	}
	
}
