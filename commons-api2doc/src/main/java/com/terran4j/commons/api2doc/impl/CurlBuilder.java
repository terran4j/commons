package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.KeyedList;
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

    private static final String enter = "\\\n";

    public static String toCurl(ApiDocObject docObject, String serverURL) {

        final List<ApiParamObject> allParams = docObject.getParams();
        final KeyedList<String, String> headers = new KeyedList<>();
        final KeyedList<String, String> params = new KeyedList<>();
        final KeyedList<String, String> cookies = new KeyedList<>();
        final Map<String, String> pathVars = new HashMap<>();

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

        StringBuilder sb = new StringBuilder("curl ");
        sb.append(enter);

        // 将 Header 参数拼接起来。
        if (headers.size() > 0) {
            for (int i = 0; i < headers.size(); i++) {
                String key = headers.getKey(i);
                String value = headers.get(i);
                sb.append(" -H \"").append(key).append(": ").append(value)
                        .append("\" ").append(enter);
            }
        }

        if (cookies.size() > 0) {
            sb.append(" -b \"");
            sb.append(joinText(cookies, ";", "="));
            sb.append("\" ").append(enter);
        }

        // 将 URL 中的 {xx} 变量用参数的示例值代替。
        String url = serverURL + docObject.getPaths()[0];
        if (pathVars.size() > 0) {
            ValueSource<String, String> vars = new ValueSource<String, String>() {
                @Override
                public String get(String key) {
                    return encode(pathVars.get(key));
                }
            };
            url = Strings.format(url, vars, "{", "}", null);
        }

        // 将“参数”拼起来。
        if (params.size() > 0) {
            RequestMethod[] requestMethods = docObject.getMethods();
            if (requestMethods.length == 1 && requestMethods[0] == RequestMethod.POST) {
                sb.append(" -d \"").append(joinText(params, "&", "="))
                        .append("\" ").append(enter);
            } else {
                url += ("?" + joinText(params, "&", "="));
            }
        }

        // 将 URL 拼接起来。
        sb.append(" \"").append(url).append("\"");

        String curl = sb.toString();
        if (log.isInfoEnabled()) {
            log.info("doc[{}]'s curl:\n{}", docObject.getId(), curl);
        }
        return curl;
    }

    /**
     * 连接成字符串，并将 value 值进行 URL 编码。
     */
    public static final String joinText(KeyedList<String, String> params,
                                        String joiner, String splitter) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = params.get(key);
            if (!first) {
                sb.append(joiner);
            }
            sb.append(key).append(splitter).append(encode(value));
            first = false;
        }
        return sb.toString();
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, Encoding.UTF8.getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
