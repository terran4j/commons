package com.terran4j.commons.hi;

import com.terran4j.commons.util.error.BusinessException;

public class HttpException extends BusinessException {

	private static final long serialVersionUID = 3426759523831086859L;

	public HttpException(HttpErrorCode code) {
		super(code.getName());
	}

	public HttpException(HttpErrorCode code, Throwable cause) {
		super(code, cause);
	}
	
}
