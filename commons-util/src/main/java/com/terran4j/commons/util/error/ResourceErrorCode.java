package com.terran4j.commons.util.error;

import java.io.IOException;
import java.util.Locale;

import com.terran4j.commons.util.value.ResourceBundlesProperties;

public class ResourceErrorCode implements ErrorCode {
	
	private final int value;
	
	private final String name;
	
	private final String message;
	
	public ResourceErrorCode(String name, Locale locale) {
		super();
		this.value = name.hashCode();
		this.name = name;
		
		ResourceBundlesProperties props = null;
		try {
			props = ResourceBundlesProperties.get("error", locale);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.message = props == null ? null : props.get(name);
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
}
