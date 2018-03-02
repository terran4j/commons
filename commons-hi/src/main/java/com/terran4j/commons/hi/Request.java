package com.terran4j.commons.hi;

import com.google.gson.*;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.JsonValueSource;
import com.terran4j.commons.util.value.ValueSource;
import com.terran4j.commons.util.value.ValueSources;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public final class Request {

    private static final JsonParser parser = new JsonParser();

    private final Session session;

    private final ApplicationContext applicationContext;

    private final Action action;

    private final Map<String, String> params = new HashMap<>();

    private final Map<String, String> inputs = new HashMap<>();

//	private final Map<String, String> expects = new HashMap<>();

    private final ValueSources<String, String> context;

    public Request(Action action, Session session, ApplicationContext applicationContext) {
        super();
        this.action = action;
        this.session = session;
        this.applicationContext = applicationContext;
        this.context = buildContext();
    }

    private final ValueSources<String, String> buildContext() {
        ValueSources<String, String> context = new ValueSources<>();

        // 从 spring 环境配置中取值。
        final ValueSource<String, String> springContext = key -> {
            String value = applicationContext.getEnvironment().getProperty(key);
            return value;
        };
        context.push(springContext);

        // 从本地变量 locals 中取值。
        final ValueSource<String, String> sessionContext = key -> {
            String value = session.getLocals().get(key);
            if (value == null) {
                return null;
            }
            return Strings.format(value, springContext);
        };
        context.push(sessionContext);

        // 从输入项中取值。
        final ValueSource<String, String> inputContext = key -> {
            String value = inputs.get(key);
            return value;
        };
        context.push(inputContext);

        return context;
    }

    public Request params(Properties props) {
        if (props != null && !props.isEmpty()) {
            Iterator<Object> it = props.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                Object value = props.get(key);
                if (key instanceof String && value instanceof String) {
                    params.put((String) key, (String) value);
                }
            }
        }
        return this;
    }

    public Request param(String key, String value) {
        params.put(key, value);
        return this;
    }

    public Request input(String key, String value) {
        inputs.put(key, value);
        return this;
    }

//	public Request expect(String key, String expectedValue) {
//		if (expectedValue == null) {
//			throw new NullPointerException("expectedValue is null.");
//		}
//		expects.put(key, expectedValue);
//		return this;
//	}

    public void exe(final int threadCount, final int exeCountPerThread,
                    final int intervalTime) throws HttpException {
        if (threadCount < 1) {
            throw new InvalidParameterException("threadCount must more than 0: " + threadCount);
        }
        if (exeCountPerThread < 1) {
            throw new InvalidParameterException("exeCountPerThread must more than 0: " + exeCountPerThread);
        }
        if (intervalTime < 0) {
            throw new InvalidParameterException("intervalTime must more than -1: " + intervalTime);
        }
        Thread[] threads = new Thread[threadCount];
        final List<HttpException> errors = new ArrayList<HttpException>();
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    for (int k = 0; k < exeCountPerThread; k++) {
                        try {
                            Thread.sleep(intervalTime);
                        } catch (InterruptedException e) {
                            // ignore.
                        }
                        try {
                            exe();
                        } catch (HttpException e) {
                            errors.add(e);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }

            });
            threads[i].start();
        }

        // 等待所有线程结束。
        try {
            latch.await();
        } catch (InterruptedException e) {
        }

        if (errors.size() > 0) {
            for (HttpException error : errors) {
                error.printStackTrace();
            }
        }
    }

    public String getContextValue(String key) {
        return context.get(key);
    }

    public String parseValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return Strings.format(value, context, "{", "}", null);
    }

    public String getActualURL() {
        String url = action.getUrl();
        String actualURL = parseValue(url);
        if (!actualURL.startsWith("http")) {
            actualURL = getURLPrefix() + actualURL;
        }
        return actualURL;
    }

    private String getURLPrefix() {
        HttpClient httpClient = action.getHttpClient();
        int port = httpClient.getPort();
        if (port == 80) {
            return "http://" + httpClient.getHost();
        } else {
            return "http://" + httpClient.getHost() + ":" + port;
        }
    }

    /**
     * @return 实际的参数值
     */
    public Map<String, String> getActualParams() {
        // 从入参中取值。
        final Map<String, String> actualParams = new HashMap<>();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.get(key);
            if (value != null) {
                String actualValue = parseValue(value);
                actualParams.put(key, actualValue);
            }
        }

        Map<String, String> actionParams = parseValues(action.getParams());
        actionParams.putAll(actualParams);
        return actionParams;
    }

    public Map<String, String> getActualHeaders() {
        Map<String, String> headers = action.getHeaders();
        return parseValues(headers);
    }

    private Map<String, String> parseValues(Map<String, String> map) {
        Map<String, String> newMap = new HashMap<>();
        if (map != null) {
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = map.get(key);
                String actualValue = parseValue(value);
                newMap.put(key, actualValue);
            }
        }
        return newMap;
    }

    public Response exe() throws HttpException {

        // 获取实际的 URL。
        String actualURL = getActualURL();
        HttpRequest request = new HttpRequest(actualURL);

        // 获取实际的入参。
        final Map<String, String> actualParams = getActualParams();
        request.setParam(actualParams);

        RequestMethod method = RequestMethod.GET;
        String methodName = action.getMethod();
        if (StringUtils.hasText(methodName)) {
            method = RequestMethod.valueOf(methodName);
            if (method == null) {
                String msg = String.format("http method[%s] not supported in action: %s",
                        methodName, action.getId());
                throw new UnsupportedOperationException(msg);
            }
        }
        request.setMethod(method);

        Map<String, String> actualHeaders = getActualHeaders();
        request.setHeaders(actualHeaders);

        List<HttpClientListener> listeners = action.getHttpClient().getListeners();
        for (HttpClientListener listener : listeners) {
            listener.beforeExecute(request);
        }
        String response = request.execute();
        for (HttpClientListener listener : listeners) {
            response = listener.afterExecute(request, response);
        }

        JsonElement result;
        try {
            result = parser.parse(response);
        } catch (JsonSyntaxException e) {
            // 不是 json 串，就按普通的字符串来处理。
            result = new JsonPrimitive(response);
        }

        List<Write> writes = action.getWrites();
        if (writes != null && writes.size() > 0) {
            JsonObject resultObject = null;
            if (result.isJsonObject()) {
                resultObject = result.getAsJsonObject();
            }
            context.push(new JsonValueSource(resultObject));
            for (Write write : writes) {
                write.doWrite(session, context);
            }
            context.pop();
        }

        return new Response(result, session);
    }

}