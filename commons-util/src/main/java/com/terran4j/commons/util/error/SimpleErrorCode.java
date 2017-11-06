package com.terran4j.commons.util.error;

public class SimpleErrorCode implements ErrorCode {
	
	public static final ErrorCode UNKNOW = new SimpleErrorCode(0, "unknow.error");

	private final int value;

	private final String name;

	public SimpleErrorCode(int value, String name) {
		super();
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValue() {
		return this.value;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
