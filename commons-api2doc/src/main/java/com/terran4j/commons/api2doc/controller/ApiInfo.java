package com.terran4j.commons.api2doc.controller;

import java.util.Arrays;
import java.util.List;

public class ApiInfo {

    String[] methods;

    String defaultMethod;

    String url;

    List<ApiEntry> params;

    List<ApiEntry> headers;

    public String[] getMethods() {
        return methods;
    }

    public void setMethods(String[] methods) {
        this.methods = methods;
    }

    public String getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(String defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ApiEntry> getParams() {
        return params;
    }

    public void setParams(List<ApiEntry> params) {
        this.params = params;
    }

    public List<ApiEntry> getHeaders() {
        return headers;
    }

    public void setHeaders(List<ApiEntry> headers) {
        this.headers = headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiInfo apiInfo = (ApiInfo) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(methods, apiInfo.methods)) return false;
        if (defaultMethod != null ? !defaultMethod.equals(apiInfo.defaultMethod) : apiInfo.defaultMethod != null)
            return false;
        if (url != null ? !url.equals(apiInfo.url) : apiInfo.url != null) return false;
        if (params != null ? !params.equals(apiInfo.params) : apiInfo.params != null) return false;
        return headers != null ? headers.equals(apiInfo.headers) : apiInfo.headers == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(methods);
        result = 31 * result + (defaultMethod != null ? defaultMethod.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }
}
