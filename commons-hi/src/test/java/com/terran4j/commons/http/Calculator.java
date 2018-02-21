package com.terran4j.commons.http;

public class Calculator {

	private final long a;
	
	private final long b;
	
	private long result;

	public Calculator(long a, long b) {
		super();
		this.a = a;
		this.b = b;
	}
	
	public long getA() {
		return a;
	}

	public long getB() {
		return b;
	}

	public long getResult() {
		return result;
	}

	public Calculator plus() {
		result = a + b;
		return this;
	}
}
