package com.terran4j.commons.hi;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.terran4j.commons.util.value.JsonValueSource;

public final class Response {
	
	private final Gson gson = new Gson();
	
	private final Session session;
	
	private final JsonValueSource values;
	
	public Response(JsonObject result, Session session) {
		super();
		this.session = session;
		this.values = new JsonValueSource(result);
	}
	
	public JsonObject getJsonRoot() {
		return values.getSource();
	}
	
	public JsonElement getJson(String key) {
		JsonElement jsonElement = values.getElement(key);
		return jsonElement;
	}
	
	public JsonElement getJson(String key, int index) {
		JsonElement jsonElement = values.getElement(key);
		if (!jsonElement.isJsonArray()) {
			return null;
		}
		return jsonElement.getAsJsonArray().get(index);
	}
	
	public <T> T getObject(String key, Class<T> clazz) {
		JsonElement jsonElement = values.getElement(key);
		if (jsonElement == null) {
			return null;
		}
		return gson.fromJson(jsonElement, clazz);
	}

	public Response assertEqual(String key, String expectalue) {
		String actualValue = values.get(key);
		org.junit.Assert.assertEquals(expectalue, actualValue);
		return this;
	}
	
	public Response assertContains(String key) {
		String actualValue = values.get(key);
		Assert.isTrue(!StringUtils.isEmpty(actualValue), "reponse should contains key: " + key);
		return this;
	}
	
//	public Response save(String key, String alias) {
//		String actualValue = values.get(key);
//		session.setAttribute(alias, actualValue);
//		return this;
//	}
//
//	public Response save(String key) {
//		String actualValue = values.get(key);
//		session.setAttribute(key, actualValue);
//		return this;
//	}
	
	public String getByPath(String path) {
		return values.get(path);
	}
	
	public long getByPath(String path, long defaultValue) {
		try {
			return Long.parseLong(values.get(path));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

}