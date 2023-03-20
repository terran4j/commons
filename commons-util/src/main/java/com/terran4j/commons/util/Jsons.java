package com.terran4j.commons.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.*;
import com.terran4j.commons.util.config.ConfigElement;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class Jsons {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

    private static final ObjectMapper objectMapper = createObjectMapper();

    public static final ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

        // 属性为空时（包括 null, 空串，空集合，空对象），不参与序列化。
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Date 对象在序列化时，格式为 yyyy-MM-dd HH:mm:ss 。
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        // json串以良好的格式输出。
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // 当属性为空或有问题时不参与序列化。
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 未知的属性不参与反序列化。
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    public static final ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static final JsonElement parseJson(String jsonText) {
        return parser.parse(jsonText);
    }

    /**
     * 将xml转化为json
     *
     * @param element XML对象
     * @return JSON对象
     */
    public static final JsonElement toJson(ConfigElement element) {
        if (element == null) {
            return null;
        }

        JsonObject json = new JsonObject();

        json.add("tag", new JsonPrimitive(element.getName()));

        String value = element.getValue();
        if (value != null) {
            json.add("value", new JsonPrimitive(value));
        }

        Set<String> attr = element.attrSet();
        if (attr != null && attr.size() > 0) {
            JsonObject jsonAttr = new JsonObject();
            Iterator<String> it = attr.iterator();
            while (it.hasNext()) {
                String attrKey = it.next();
                String attrValue = element.attr(attrKey);
                if (attrKey != null && attrValue != null) {
                    jsonAttr.add(attrKey, new JsonPrimitive(attrValue));
                }
            }
            json.add("attr", jsonAttr);
        }

        ConfigElement[] children = element.getChildren();
        if (children != null && children.length > 0) {
            JsonArray jsonChildren = new JsonArray();
            for (ConfigElement child : children) {
                JsonElement jsonChild = toJson(child);
                if (jsonChild != null) {
                    jsonChildren.add(jsonChild);
                }
            }
            json.add("children", jsonChildren);
        }

        return json;
    }

    public static String format(JsonElement json) {
        String prettyJsonText = gson.toJson(json);
        return prettyJsonText;
    }

    /**
     * 格式化
     *
     * @param uglyJsonText 未格式化的 json 串。
     * @return 格式化的 json 串。
     */
    public static String format(String uglyJsonText) {
        JsonElement je = parser.parse(uglyJsonText);
        String prettyJsonText = format(je);
        return prettyJsonText;
    }

    public static Map<String, Object> toMap(JsonObject json) {
        if (json == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        Iterator<Map.Entry<String, JsonElement>> it = json.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, JsonElement> entry = it.next();
            String key = entry.getKey();
            JsonElement element = entry.getValue();
            Object value = toObject(element);
            result.put(key, value);
        }
        return result;
    }

    public static String toJsonText(Object obj) throws JsonProcessingException {
        if (obj == null) {
            return null;
        }

        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T toObject(String jsonText, Class<T> clazz) throws JsonProcessingException {
        if (StringUtils.isBlank(jsonText)) {
            return null;
        }
        return objectMapper.readValue(jsonText, clazz);
    }

    public static Object toObject(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        Object value = null;
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                value = primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                value = primitive.getAsNumber();
            } else if (primitive.isString()) {
                value = primitive.getAsString();
            }
        } else if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            JsonArray jsonArray = element.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement child = jsonArray.get(i);
                list.add(toObject(child));
            }
            value = list;
        } else if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            Map<String, Object> map = new HashMap<>();
            Iterator<Map.Entry<String, JsonElement>> it = jsonObject.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, JsonElement> entry = it.next();
                String key = entry.getKey();
                JsonElement child = entry.getValue();
                map.put(key, toObject(child));
            }
            value = map;
        }

        return value;
    }

    public static <T> T readJson(Class<T> clazz, File json) throws IOException {
        Gson g = new GsonBuilder().disableHtmlEscaping().create();
        Reader reader = null;
        try{
            reader = new InputStreamReader(new FileInputStream(json), StandardCharsets.UTF_8);
            return g.fromJson(reader, clazz);
        }finally {
            if(reader != null)reader.close();
        }
    }

    public static <T> void writeJson(T object, File file) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String content = g.toJson(object);
        IOUtils.setFileContent(file, content);
    }


}
