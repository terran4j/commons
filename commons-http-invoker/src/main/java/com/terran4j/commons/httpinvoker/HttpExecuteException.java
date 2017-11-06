package com.terran4j.commons.httpinvoker;

import com.terran4j.commons.util.error.BusinessException;

public class HttpExecuteException extends BusinessException {

	private static final long serialVersionUID = 3426759523831086859L;

	public HttpExecuteException(HttpErrorCode code) {
		super(code);
	}

	public HttpExecuteException(HttpErrorCode code, Throwable cause) {
		super(code, cause);
	}
	
}
