package com.terran4j.commons.util.value;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.terran4j.commons.util.Strings;

public final class RichProperties implements ValueSource<String, String>{
	
	private static final Object NULL = new Object();

	private final Map<String, Object> props = new ConcurrentHashMap<String, Object>();

	private String message;
	
	public RichProperties setMessage(String message) {
		this.message = message;
		return this;
	}

	public RichProperties() {
		super();
	}

	public RichProperties(String message) {
		super();
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public final String getMessage() {
		return Strings.format(message, this);
	}

	public void put(String key, Object value) {
		if (value == null) {
			value = NULL;
		}
		props.put(key, value);
	}

	public Object getObject(String key) {
		Object value = props.get(key);
		if (value == NULL) {
			return null;
		}
		return value;
	}
	
	public int size() {
		return props.size();
	}
	
	public Iterator<String> iterator() {
		return props.keySet().iterator();
	}

	@Override
	public String get(String key) {
		Object value = props.get(key);
		return Objects.toString(value);
	}
	
	public Map<String, Object> getAll() {
		return props;
	}
}