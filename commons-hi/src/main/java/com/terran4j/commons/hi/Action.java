package com.terran4j.commons.hi;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Action {

    private HttpClient httpClient;

    private String id;

    private String name;

    private String url;

    private String method = RequestMethod.GET.name();

    private Map<String, String> params = new HashMap<>();

    private Map<String, String> headers = new HashMap<>();

    private List<Write> writes = new ArrayList<>();

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

    public String param(String key) {
        return String.valueOf(params.get(key));
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String header(String key) {
        return String.valueOf(headers.get(key));
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public List<Write> getWrites() {
        return writes;
    }

    public void setWrites(List<Write> writes) {
        this.writes = writes;
    }

}
