package com.terran4j.commons.test.restpack;

import java.util.Date;

public class HelloBean {
	
	private String name;
	
	private String message;
	
	private Date time;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public final Date getTime() {
		return time;
	}

	public final void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "Hello [name=" + name + ", message=" + message + ", time=" + time + "]";
	}
	
}