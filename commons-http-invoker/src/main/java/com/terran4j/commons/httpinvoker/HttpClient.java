package com.terran4j.commons.httpinvoker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.JsonValueSource;
import com.terran4j.commons.util.value.ValueSource;

public class HttpClient {
	
	private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

	private static final Gson gson = new Gson();
	
	private ApplicationContext context;

	private JsonObject config;
	
	private List<HttpClientListener> listeners = new ArrayList<>();
	
	private Map<String, ValueSource<String, String>> environments = new HashMap<>();
	
	private Map<String, String> locals = new HashMap<>();

	private Map<String, String> params = new HashMap<>();

	private Map<String, String> headers = new HashMap<>();

	private Map<String, Action> actions = new HashMap<String, Action>();
	
	public void addListener(HttpClientListener listener) {
		listeners.add(listener);
	}
	
	public List<HttpClientListener> getListeners() {
		List<HttpClientListener> temp = new ArrayList<>();
		temp.addAll(listeners);
		return temp;
	}
	
	public static final HttpClient create(Class<?> clazz, String fileName, ApplicationContext context) {
		String json = Strings.getString(clazz, fileName);
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(json);
		JsonObject config = element.getAsJsonObject();
		
		return create(config, context);
	}
	
	public static final HttpClient create(JsonObject config, ApplicationContext context) {
		if (config == null) {
			return null;
		}
		
		return new HttpClient(config, context);
	}
	
	public static final HttpClient create(ApplicationContext context, File file) {
		InputStream in = null;
		String json = null;
		try {
			in = new FileInputStream(file);
			json = Strings.getString(in, Encoding.UTF8);
		} catch (Exception e) {
			log.error("load file[{}] error: {}", file, e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		
		if (StringUtils.isEmpty(json)) {
			log.error("load http.config.json failed.");
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(json);
		JsonObject config = element.getAsJsonObject();
		
		return create(config, context);
	}
	
	public static final HttpClient create(ApplicationContext context) {
		return create(null, "http.config.json", context);
	}

//	public HttpService() {
//		super();
//		String json = Strings.getString(null, "http.config.json");
//		if (!StringUtils.isEmpty(json)) {
//			JsonParser parser = new JsonParser();
//			JsonElement element = parser.parse(json);
//			JsonObject config = element.getAsJsonObject();
//			init(config);
//		}
//	}
	
	private HttpClient(JsonObject config, ApplicationContext context) {
		super();
		setApplicationContext(context);
		init(config);
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
		if (log.isInfoEnabled()) {
			log.info("setApplicationContext done.");
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void init(JsonObject config) {
		JsonElement element = config.get("environments");
		if (element != null) {
			JsonObject envs = element.getAsJsonObject();
			Set<Entry<String, JsonElement>> set = envs.entrySet();
			if (set != null && set.size() > 0) {
				for (Entry<String, JsonElement> entry : set) {
					String key = entry.getKey();
					JsonElement data = entry.getValue();
					JsonValueSource values = new JsonValueSource(data.getAsJsonObject());
					environments.put(key, values);
				}
			}
		}
		
		element = config.get("params");
		if (element != null) {
			params = gson.fromJson(element, Map.class);
		}

		element = config.get("headers");
		if (element != null) {
			headers = gson.fromJson(element, Map.class);
		}
		
		element = config.get("locals");
		if (element != null) {
			locals = gson.fromJson(element, Map.class);
		}

		element = config.get("actions");
		if (element != null) {
			actions = new HashMap<String, Action>();
			JsonArray array = element.getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				Action invoker = gson.fromJson(array.get(i), Action.class);
				invoker.setHttpClient(this);
				actions.put(invoker.getId(), invoker);
			}
		}
	}

	public JsonObject getConfig() {
		return config;
	}

	public void setConfig(JsonObject config) {
		this.config = config;
	}

	public Map<String, String> cloneParams() {
		Map<String, String> cloneParams = new HashMap<String, String>();
		cloneParams.putAll(params);
		return cloneParams;
	}

	public Map<String, String> cloneHeaders() {
		Map<String, String> cloneHeaders = new HashMap<String, String>();
		cloneHeaders.putAll(headers);
		return cloneHeaders;
	}
	
	public ValueSource<String, String> getEnvironment(String profile) {
		return environments.get(profile);
	}
	
	public Map<String, String> cloneLocals() {
		Map<String, String> cloneLocals = new HashMap<String, String>();
		cloneLocals.putAll(locals);
		return cloneLocals;
	}

	public Map<String, Action> getActions() {
		return actions;
	}

	public Session create() {
		return new Session(this, context);
	}

	public ApplicationContext getApplicationContext() {
		return context;
	}

}
