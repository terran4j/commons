package com.terran4j.commons.util.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonValueSource implements ValueSource<String, String> {
	
	private final JsonObject result;
	
	public JsonValueSource(JsonObject result) {
		super();
		this.result = result;
	}
	
	public JsonObject getSource() {
		return this.result;
	}

	@Override
	public String get(String key) {
		if (result == null) {
			return null;
		}
		String[] array = key.split("\\.");
		JsonElement current = result;
		for (String item : array) {
			JsonElement element = current.getAsJsonObject().get(item);
			if (element == null) {
				return null;
			}
			current = element;
		}
		return current.getAsJsonPrimitive().getAsString();
	}
	
	public JsonElement getElement(String key) {
		if (result == null) {
			return null;
		}
		String[] array = key.split("\\.");
		JsonElement current = result;
		for (String item : array) {
			JsonElement element = current.getAsJsonObject().get(item);
			if (element == null) {
				return null;
			}
			current = element;
		}
		return current;
	}
	
}
