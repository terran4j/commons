package com.terran4j.commons.hi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.terran4j.commons.api2doc.meta.ClassMeta;
import com.terran4j.commons.api2doc.meta.MethodMeta;
import com.terran4j.commons.api2doc.meta.ParamMeta;
import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Jsons;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Api2DocSupport {

    private static final Logger log = LoggerFactory.getLogger(Api2DocSupport.class);

    private static final Gson gson = new Gson();

    public static final JsonObject loadConfig(String host, int port) throws IOException {
        String url = "http://" + host + ":" + port;
        String api2docURL = url + "/api2doc/meta/classes";
        JsonArray classMetas = loadClassMetas(api2docURL);
        JsonObject config = toConfig(classMetas);
        config.addProperty("url", url);
        return config;
    }

    static final JsonObject toConfig(JsonArray classMetas) {
        JsonObject config = new JsonObject();

        JsonArray actions = new JsonArray();
        for (int i = 0; i < classMetas.size(); i++) {
            JsonObject classMetaJson = classMetas.get(i).getAsJsonObject();
            ClassMeta classMeta = gson.fromJson(classMetaJson, ClassMeta.class);

            String classId = classMeta.getId();
            List<MethodMeta> methods = classMeta.getMethods();
            for (int j = 0; j < methods.size(); j++) {
                MethodMeta method = methods.get(j);
                JsonObject action = toAction(method, classId);
                actions.add(action);
            }
        }

        config.add("actions", actions);
        return config;
    }

    static final JsonObject toAction(MethodMeta method, String classId) {
        JsonObject action = new JsonObject();

        String methodId = method.getId();
        String id = classId + "-" + methodId;
        action.addProperty("id", id);

        String name = method.getName();
        action.addProperty("name", name);

        String url = method.getPaths()[0];
        action.addProperty("url", url);

        String requestMethod = method.getRequestMethods()[0];
        action.addProperty("method", requestMethod);

        JsonObject headers = new JsonObject();
        action.add("headers", headers);

        JsonObject params = new JsonObject();
        action.add("params", params);

        List<ParamMeta> paramMetas = method.getParams();
        for (ParamMeta paramMeta : paramMetas) {
            String key = paramMeta.getId();
            String value = "{" + key + "}";
            String location = paramMeta.getLocation();
            if ("RequestHeader".equals(location)) {
                headers.addProperty(key, value);
            }
            if ("PathVariable".equals(location)) {
                continue;
            }
            if ("RequestParam".equals(location)) {
                params.addProperty(key, value);
            }
        }

        return action;
    }

    static final JsonArray loadClassMetas(String url) throws IOException {
        HttpClient httpClient = ApacheHttpClientBuilder.build(
                1000 * 30, Encoding.UTF8.getName());


        URIBuilder uriBuilder;
        URI uri;
        try {
            uriBuilder = new URIBuilder(url);
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("build url failed: " + url, e);
        }
        final HttpGet httpGet = new HttpGet(uri);
        String response = httpClient.execute(httpGet,
                new ApacheHttpClientBuilder.DefaultResponseHandler());
        if (log.isInfoEnabled()) {
            log.info("load api2doc meta info:{}", response);
        }

        JsonObject root;
        String resultCode;
        JsonArray data;
        try {
            JsonElement json = Jsons.parseJson(response);
            root = json.getAsJsonObject();
            resultCode = root.get("resultCode").getAsString();
            data = root.get("data").getAsJsonArray();
        } catch (RuntimeException e) {
            throw new RuntimeException("parse json failed: " + e.getMessage(), e);
        }

        if (!"success".equals(resultCode)) {
            String message = root.get("message").getAsString();
            throw new RuntimeException("load api2doc meta failed: " + message);
        }

        return data;
    }

}
