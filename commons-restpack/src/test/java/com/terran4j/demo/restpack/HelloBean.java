package com.terran4j.demo.restpack;

import com.terran4j.commons.restpack.RestPackIgnore;
import com.terran4j.commons.util.Strings;

import java.util.Date;

public class HelloBean {
	
	private String name;
	
	private String message;
	
	private Date time;

	@RestPackIgnore
	private Boolean deleted = false;

    @RestPackIgnore
	private long count = 1;

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

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return Strings.toString(this);
	}
	
}