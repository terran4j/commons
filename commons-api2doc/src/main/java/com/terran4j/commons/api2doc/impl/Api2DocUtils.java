package com.terran4j.commons.api2doc.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiObject;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.restpack.RestPackIgnore;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.value.ValueSource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
                    String value = param.getSample().getValue();
                    if (StringUtils.isEmpty(value)) {
                        value = param.getDataType().getDefault();
                    }
                    pathParams.put(param.getId(), value);
                }
                if (param.getLocation() == ApiParamLocation.Param) {
                    String value = param.getSample().getValue();
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

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, Encoding.UTF8.getName());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

//    public static final void setApiComment(ApiComment apiComment, ApiObject apiObject) {
//        if (apiComment != null && StringUtils.hasText(apiComment.value())) {
//            String commentText = getComment(apiComment);
//            apiObject.setComment(commentText);
//        }
//        if (apiComment != null && StringUtils.hasText(apiComment.sample())) {
//            if (StringUtils.hasText(apiComment.sample())) {
//                apiObject.setSample(apiComment.sample());
//            }
//        }
//    }
//
//    public static final String getComment(ApiComment apiComment) {
//        String comment = apiComment.value();
//        if (StringUtils.isEmpty(comment)) {
//            return null;
//        }
//        return comment.trim();
//    }
//
//    public static final String getSample(ApiComment apiComment, Class<?> clazz) throws BusinessException {
//        String sample = apiComment.sample();
//        if (StringUtils.isEmpty(sample)) {
//            return null;
//        }
//        return sample.trim();
//
////        if (!(sample.startsWith("@") && sample.endsWith("@"))) {
////            return sample.replaceAll("\n", "<br/>");
////        }
////
////        String fileName = sample.substring(1, sample.length() - 1);
////        String json = Strings.getString(clazz, fileName);
////        if (StringUtils.isEmpty(json)) {
////            throw new BusinessException(ErrorCodes.CONFIG_ERROR)
////                    .put("package", clazz.getPackage().getName())
////                    .put("fileName", fileName)
////                    .setMessage("在包 ${package} 中找不到文件： ${fileName}");
////        }
////        return json;
//    }

    public static final Class<?> getArrayElementClass(Method method) {

        Class<?> returnType = method.getReturnType();
        if (returnType.isArray()) {
            Class<?> elementClass = returnType.getComponentType();
            return elementClass;
        }

        if (Classes.isInterface(returnType, Collection.class)) {
            Type gType = method.getGenericReturnType();
            Type elementType = getGenericType(gType);
            if (elementType instanceof Class<?>) {
                Class<?> elementClass = (Class<?>) elementType;
                return elementClass;
            }
        }

        return null;
    }

    public static final Type getGenericType(Type gType) {
        // 如果gType是泛型类型对像。
        if (gType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) gType;
            // 获得泛型类型的泛型参数
            Type[] gArgs = pType.getActualTypeArguments();
            return gArgs[gArgs.length - 1];
        } else {
            System.out.println("获取泛型信息失败");
            return null;
        }
    }

    public static final boolean isFilter(
            PropertyDescriptor prop, Class<?> clazz) {

        String fieldName = prop.getName();

        // class 只是 Java 对象自带的  Object.getClass() 方法，忽略掉。
        if ("class".equals(fieldName)) {
            return true;
        }

        // 忽略掉需要忽略的字段。
        Field field = Classes.getField(fieldName, clazz);
        if (field != null) {
            if (field.getAnnotation(RestPackIgnore.class) != null) {
                return true;
            }
            if (field.getAnnotation(JsonIgnore.class) != null) {
                return true;
            }
        }

        return false;
    }
}
