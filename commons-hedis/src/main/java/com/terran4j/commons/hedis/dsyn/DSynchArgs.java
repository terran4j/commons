package com.terran4j.commons.hedis.dsyn;

import java.util.HashMap;
import java.util.Map;

public class DSynchArgs {

	private static final ThreadLocal<Map<String, Object>> buffer = new ThreadLocal<>();
	
	public static final void set(String key, Object value) {
		Map<String, Object> args = buffer.get();
		if (args == null) {
			args = new HashMap<>();
			buffer.set(args);
		}
		args.put(key, value);
	}
	
	public static final Object get(String key) {
		Map<String, Object> args = buffer.get();
		if (args == null) {
			return null;
		}
		return args.get(key);
	}
	
	public static final void clear() {
		buffer.set(null);
	}
}
