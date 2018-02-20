package com.terran4j.commons.jfinger;

import com.terran4j.commons.util.error.BusinessException;

public class CommandException extends BusinessException {

	private static final long serialVersionUID = -691993852577468850L;

	public CommandException(String code) {
		super(code);
	}

	public CommandException(String code, Throwable cause) {
		super(code, cause);
	}
	
	

}