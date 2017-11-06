package com.terran4j.common.util.error;

import com.terran4j.commons.util.error.ErrorCode;

public enum MockErrorCode implements ErrorCode {
	
	INVALID_CONFIG_VALUE(101, "invalid.config.value"),
	
	;

	private final int value;
	
	private final String name;

	private MockErrorCode(int value, String name) {
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
