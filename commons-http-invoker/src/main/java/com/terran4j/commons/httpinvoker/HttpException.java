package com.terran4j.commons.httpinvoker;

import com.terran4j.commons.util.error.BusinessException;

public class HttpException extends BusinessException {

	private static final long serialVersionUID = 3426759523831086859L;

	public HttpException(HttpErrorCode code) {
		super(code);
	}

	public HttpException(HttpErrorCode code, Throwable cause) {
		super(code, cause);
	}
	
}
