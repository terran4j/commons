package com.terran4j.commons.hi;

import com.terran4j.commons.util.error.ErrorCode;

public enum HttpErrorCode implements ErrorCode {
	
	HTTP_REQUEST_ERROR(1, "http.request.error"),
	
	EXPECT_FAILED(2, "expect.failed"),
	
	ACTION_NOT_FOUND(3, "action.not.found"),
	
	UNSUPPORTED_METHOD(4, "unsupported.method"),
	
	URI_SYNTAX_ERROR(5, "uri.syntax.error"),
	;
	

	private final int value;
	
	private final String name;

	private HttpErrorCode(int value, String name) {
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
