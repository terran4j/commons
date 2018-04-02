package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.domain.ApiDataType;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.KeyedList;
import com.terran4j.commons.util.value.MapValueSource;
import com.terran4j.commons.util.value.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CurlBuilder {

    private static final Logger log = LoggerFactory.getLogger(CurlBuilder.class);

    public static String toCurl(ApiDocObject docObject, String serverURL) {
        StringBuilder sb = new StringBuilder("curl");

        List<ApiParamObject> allParams = docObject.getParams();
        KeyedList<String, String> headers = new KeyedList<>();
        KeyedList<String, String> params = new KeyedList<>();
        KeyedList<String, String> cookies = new KeyedList<>();
        Map<String, String> pathVars = new HashMap<>();

        if (allParams.size() > 0) {
            for (ApiParamObject param : allParams) {
                String key = param.getId();

                String value = param.getSample().getValue();
                if (StringUtils.isEmpty(value)) {
                    Class<?> paramType = param.getSourceType();
                    if (paramType == String.class) {
                        value = key;
                    } else {
                        value = param.getDataType().getDefault();
                    }
                }

                if (param.getLocation() == ApiParamLocation.RequestParam) {
                    params.add(key, value);
                }
                if (param.getLocation() == ApiParamLocation.PathVariable) {
                    pathVars.put(key, value);
                }
                if (param.getLocation() == ApiParamLocation.RequestHeader) {
                    headers.add(key, value);
                }
                if (param.getLocation() == ApiParamLocation.CookieValue) {
                    cookies.add(key, value);
                }
            }
        }

        // 将 Header 参数拼接起来。
        if (headers.size() > 0) {
            for (int i = 0; i < headers.size(); i++) {
                String key = headers.getKey(i);
                String value = headers.get(i);
                sb.append(" -H \"").append(key).append(": ").append(value).append("\"");
            }
        }

        // 将 URL 中的 {xx} 变量用参数的示例值代替。
        String url = serverURL + docObject.getPaths()[0];
        if (pathVars.size() > 0) {
            ValueSource<String, String> vars = new MapValueSource<>(pathVars);
            url = Strings.format(url, vars, "{", "}", null);
        }

        // 将“参数”及 URL 拼起来。
        if (params.size() > 0) {
            RequestMethod[] requestMethods = docObject.getMethods();
            if (requestMethods.length == 1 &&
                    requestMethods[0] == RequestMethod.POST) {
                sb.append(" -d \"").append(toUrlQuery(params)).append("\"");
                sb.append(" \"").append(url);
            } else {
                sb.append(" \"").append(url);
                sb.append("?").append(toUrlQuery(params));
            }
            sb.append("\"");
        }

        return sb.toString();
    }

    public static final String toUrlQuery(KeyedList<String, String> params) {
        StringBuffer query = new StringBuffer();
        boolean first = true;
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.get(key);
            if (log.isDebugEnabled()) {
                log.debug("key = {}, value = {}", key, value);
            }
            if (!first) {
                query.append("&");
            }
            query.append(key).append("=").append(encode(value));
            first = false;
        }
        if (log.isInfoEnabled()) {
            log.info("query: {}", query);
        }
        return query.toString();
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, Encoding.UTF8.getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
