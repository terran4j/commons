package com.terran4j.test.commons.reflux;

public class Hello {

	private String name;
	
	private String greeting;
	
	private long currentTime;
	
	public Hello() {
		super();
	}
	
	public Hello(String name) {
		this(name, "Hello, " + name);
	}

	public Hello(String name, String greeting) {
		super();
		this.name = name;
		this.greeting = greeting;
		this.currentTime = System.currentTimeMillis();
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the greeting
	 */
	public final String getGreeting() {
		return greeting;
	}

	/**
	 * @param greeting the greeting to set
	 */
	public final void setGreeting(String greeting) {
		this.greeting = greeting;
	}

	/**
	 * @return the currentTime
	 */
	public final long getCurrentTime() {
		return currentTime;
	}

	/**
	 * @param currentTime the currentTime to set
	 */
	public final void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	
}
