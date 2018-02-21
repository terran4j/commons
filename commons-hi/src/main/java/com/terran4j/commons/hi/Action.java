package com.terran4j.commons.hi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.JsonValueSource;
import com.terran4j.commons.util.value.ValueSource;
import com.terran4j.commons.util.value.ValueSources;

public class Action {

	private static final JsonParser parser = new JsonParser();
	
	private HttpClient httpClient;

	private String id;

	private String name;

	private String url;

	private String method = RequestMethod.GET.name();

	private Map<String, String> params = new HashMap<String, String>();

	private List<Write> writes = new ArrayList<Write>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	private String parse(String str, ValueSource<String, String> values) {
		return Strings.format(str, values, "${", "}", null);
	}

	private Map<String, String> parse(Map<String, String> map, ValueSource<String, String> values) {
		Map<String, String> newMap = new HashMap<String, String>();
		if (map != null) {
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = map.get(key);
				String actualValue = parse(value, values);
				newMap.put(key, actualValue);
			}
		}
		return newMap;
	}

	public JsonObject exe(final ValueSources<String, String> context, Session session,
			final Map<String, String> inputParams) throws HttpException {

		String actualURL = parse(url, context);
		HttpRequest request = new HttpRequest(actualURL);

		request.setMethod(RequestMethod.valueOf(method));

		Map<String, String> commonParams = parse(session.getParams(), context);
		Map<String, String> actionParams = parse(params, context);
		request.setParam(commonParams).setParam(actionParams).setParam(inputParams);

		Map<String, String> commonHeaders = parse(session.getHeaders(), context);
		request.setHeaders(commonHeaders);

		List<HttpClientListener> listeners = this.getHttpClient().getListeners();
		for (HttpClientListener listener : listeners) {
			listener.beforeExecute(request);
		}
		String reponse = request.execute();
		for (HttpClientListener listener : listeners) {
			reponse = listener.afterExecute(request, reponse);
		}
		
		JsonElement element = parser.parse(reponse);
		JsonObject result = element.getAsJsonObject();

		if (writes != null && writes.size() > 0) {
			context.push(new JsonValueSource(result));
			for (Write write : writes) {
				write.doWrite(session, context);
			}
			context.pop();
		}

		return result;
	}
}
