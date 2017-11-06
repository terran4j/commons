package com.terran4j.commons.jfinger;

import java.util.HashMap;
import java.util.Map;

public enum OptionType {

	BOOLEAN("boolean"), STRING("string"), INT("int"), PROPERTIES("properties");

	private static final Map<String, OptionType> values = new HashMap<String, OptionType>();

	static {
		OptionType[] array = OptionType.values();
		for (OptionType type : array) {
			values.put(type.getName(), type);
		}
	}

	public static final OptionType toValue(String name) {
		if (name == null) {
			return null;
		}
		return values.get(name);
	}

	private String name;

	private OptionType(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
}