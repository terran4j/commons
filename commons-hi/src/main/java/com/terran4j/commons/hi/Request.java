package com.terran4j.commons.hi;

import com.google.gson.JsonParser;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.config.ConfigElement;
import com.terran4j.commons.util.config.JsonConfigElement;
import com.terran4j.commons.util.config.XmlConfigElement;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import com.terran4j.commons.util.security.MD5Util;
import com.terran4j.commons.util.value.ValueSource;
import com.terran4j.commons.util.value.ValueSources;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
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

    private String signParamKey = null;

    private String signSecretKey = null;

    /**
     * add by mark for plain content.
     */
    private StringBuffer content = new StringBuffer();

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
            if (applicationContext == null) {
                return null;
            }
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

    public Request sign(String signParamKey, String signSecretKey) {
        if (StringUtils.isBlank(signParamKey)) {
            throw new NullPointerException("signParamKey is null.");
        }
        if (StringUtils.isBlank(signSecretKey)) {
            throw new NullPointerException("signSecretKey is null.");
        }
        this.signParamKey = signParamKey.trim();
        this.signSecretKey = signSecretKey.trim();
        return this;
    }

    public Request content(String content) {
        this.content = new StringBuffer(content);
        return this;
    }

    public Request input(String key, String value) {
        inputs.put(key, value);
        return this;
    }

    public void exe(final int threadCount, final int exeCountPerThread,
                    final int intervalTime) throws HttpException {
        if (threadCount < 1) {
            throw new InvalidParameterException(
                    "threadCount must more than 0: " + threadCount);
        }
        if (exeCountPerThread < 1) {
            throw new InvalidParameterException(
                    "exeCountPerThread must more than 0: " + exeCountPerThread);
        }
        if (intervalTime < 0) {
            throw new InvalidParameterException(
                    "intervalTime must more than -1: " + intervalTime);
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
        if (StringUtils.isBlank(value)) {
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
    public Map<String, String> getActualParams() throws BusinessException {
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

        buildSignParam(actionParams);

        return actionParams;
    }

    /**
     * @return
     */
    public StringBuffer getActualContent() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.content);
        sb.trimToSize();
        return sb;
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

    private void buildSignParam(Map<String, String> params)
            throws BusinessException {
        if (signSecretKey == null || signParamKey == null) {
            return;
        }

        if (MapUtils.isEmpty(params)) {
            return;
        }

        if (params.containsKey(signParamKey)) {
            throw new BusinessException(ErrorCodes.INVALID_PARAM)
                    .put("signParamKey", signParamKey)
                    .setMessage("参数key与签名参数重复：${signParamKey}");
        }

        String signValue = MD5Util.signature(params, signSecretKey);
        params.put(signParamKey, signValue);
    }

    public Response exe() throws BusinessException {

        // 获取实际的 URL。
        String actualURL = getActualURL();
        HttpRequest request = new HttpRequest(actualURL);

        // 获取实际的入参。
        final Map<String, String> actualParams = getActualParams();
        request.setParam(actualParams);
        request.setContent(this.content.toString());

        RequestMethod method = RequestMethod.GET;
        String methodName = action.getMethod();
        if (StringUtils.isNotBlank(methodName)) {
            method = RequestMethod.valueOf(methodName);
            if (method == null) {
                String msg = String.format("http method[%s] not supported in action: %s",
                        methodName, action.getId());
                throw new UnsupportedOperationException(msg);
            }
        }
        request.setMethod(method);

        request.setPostBody(action.getPostBody());
        request.setResponseBody(action.getResponseBody());

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

        ConfigElement root = null;
        if ("XML".equalsIgnoreCase(request.getResponseBody())) {
            root = new XmlConfigElement(response);
        } else {
            root = new JsonConfigElement(response);
        }

        List<Write> writes = action.getWrites();
        if (writes != null && writes.size() > 0) {
            context.push(root);
            for (Write write : writes) {
                write.doWrite(session, context);
            }
            context.pop();
        }

        return new Response(root, session);
    }

}