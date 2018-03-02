package com.terran4j.commons.hi;

import com.google.gson.*;
import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    private static final Gson gson = new Gson();

    private ApplicationContext context;

    private String host;

    private int port;

    private JsonObject config;

    private List<HttpClientListener> listeners = new ArrayList<>();

    /**
     * 本地变量的初始值，其值是不能变的。
     *  创建 session 时，会 copy 给 session ，session 中的 local 值可变。
     */
    private Map<String, String> locals = new HashMap<>();

    private Map<String, Action> actions = new HashMap<>();

    public void addListener(HttpClientListener listener) {
        listeners.add(listener);
    }

    public List<HttpClientListener> getListeners() {
        List<HttpClientListener> temp = new ArrayList<>();
        temp.addAll(listeners);
        return temp;
    }

    public static final HttpClient create(
            @NotNull String host, int port,
            ApplicationContext context) throws IOException {
        JsonObject config = Api2DocSupport.loadConfig(host, port);
        return create(config, context, host, port);
    }

    public static final HttpClient create(
            @NotNull Class<?> clazz, @NotNull String fileName,
            ApplicationContext context) {
        String json = Strings.getString(clazz, fileName);
        if (StringUtils.isEmpty(json)) {
            String msg = String.format(
                    "load resource[%s] in package[%s] failed.",
                    fileName, clazz.getPackage().getName());
            throw new RuntimeException(msg);
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        JsonObject config = element.getAsJsonObject();

        return create(config, context, "localhost", 8080);
    }

    public static final HttpClient create(
            @NotNull JsonObject config,
            ApplicationContext context, String host, int port) {
        return new HttpClient(config, context, host, port);
    }

    public static final HttpClient create(@NotNull File file,
                                          ApplicationContext context) {
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
            log.error("file[{}]  is empty", file);
            return null;
        }

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        JsonObject config = element.getAsJsonObject();

        return create(config, context, "localhost", 8080);
    }

    public static final HttpClient create(ApplicationContext context) {
        return create(null, "hi.json", context);
    }

    private HttpClient(JsonObject config, ApplicationContext context, String host, int port) {
        super();
        this.host = host;
        this.port = port;
        setApplicationContext(context);
        init(config);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
        if (log.isInfoEnabled()) {
            log.info("setApplicationContext done.");
        }
    }

    @SuppressWarnings({"unchecked"})
    private void init(JsonObject config) {
//        JsonElement element = config.get("environments");
//        if (element != null) {
//            JsonObject envs = element.getAsJsonObject();
//            Set<Entry<String, JsonElement>> set = envs.entrySet();
//            if (set != null && set.size() > 0) {
//                for (Entry<String, JsonElement> entry : set) {
//                    String key = entry.getKey();
//                    JsonElement data = entry.getValue();
//                    JsonValueSource values = new JsonValueSource(data.getAsJsonObject());
//                    environments.put(key, values);
//                }
//            }
//        }

        JsonElement element = config.get("locals");
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

//    public Map<String, String> cloneParams() {
//        Map<String, String> cloneParams = new HashMap<String, String>();
//        cloneParams.putAll(params);
//        return cloneParams;
//    }
//
//    public Map<String, String> cloneHeaders() {
//        Map<String, String> cloneHeaders = new HashMap<String, String>();
//        cloneHeaders.putAll(headers);
//        return cloneHeaders;
//    }
//
//    public ValueSource<String, String> getEnvironment(String profile) {
//        return environments.get(profile);
//    }
//
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String local(String key) {
        return String.valueOf(this.locals.get(key));
    }
}
