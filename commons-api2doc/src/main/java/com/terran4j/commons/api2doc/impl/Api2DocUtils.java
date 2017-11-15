package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.ValueSource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class Api2DocUtils {

    public static String toURL(ApiDocObject doc, String serverURL) {
        if (doc == null) {
            throw new NullPointerException("doc is null.");
        }

        boolean hasGetMethod = false;
        RequestMethod[] methods = doc.getMethods();
        if (methods != null) {
            for (RequestMethod method : methods) {
                if (method == RequestMethod.GET) {
                    hasGetMethod = true;
                }
            }
        } else {
            hasGetMethod = true;
        }
        if (!hasGetMethod) {
            // TODO: 暂时不支持非 GET 的请求。
            return null;
        }

        String docURL = serverURL + doc.getPaths()[0];
        List<ApiParamObject> params = doc.getParams();
        Map<String, String> pathParams = new HashMap<>();
        Map<String, String> getParams = new HashMap<>();
        if (params != null) {
            for (ApiParamObject param : params) {
                if (param.getLocation() == ApiParamLocation.Path) {
                    String value = param.getSample();
                    if (StringUtils.isEmpty(value)) {
                        value = param.getDataType().getDefault();
                    }
                    pathParams.put(param.getId(), value);
                }
                if (param.getLocation() == ApiParamLocation.Param) {
                    String value = param.getSample();
                    if (StringUtils.isEmpty(value)) {
                        continue;
                    }
                    getParams.put(param.getId(), value);
                }
            }
        }

        if (pathParams.size() > 0) {
            docURL = Strings.format(docURL, new ValueSource<String, String>() {

                @Override
                public String get(String key) {
                    String value = pathParams.get(key);
                    if (value == null) {
                        return null;
                    }
                    return encode(value);
                }
            }, "{", "}", null);
        }

        if (getParams.size() > 0) {

            List<String> keys = new ArrayList<>();
            keys.addAll(getParams.keySet());
            keys.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });

            StringBuffer sb = new StringBuffer();
            for (String key : keys) {
                String value = getParams.get(key);
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(key).append("=").append(encode(value));
            }
            docURL = docURL + "?" + sb.toString();
        }

        return docURL;
    }

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str, Encoding.UTF8.getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
