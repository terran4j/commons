package com.terran4j.commons.test;

public class HelloService {
	
	private volatile int count = 0;
	
	public String sayHello(String name) {
		count++;
		return "Hello " + name;
	}

	public int getCount() {
		return count;
	}

}