package com.terran4j.commons.util.config;

import com.google.gson.*;
import com.terran4j.commons.util.error.BusinessException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JsonConfigElement implements ConfigElement {

    private static final Gson gson = new Gson();

    private static final JsonParser parser = new JsonParser();

    private final ClassLoader classLoader;

    private JsonElement element;

    public JsonConfigElement(String jsonText) throws BusinessException {
        classLoader = Thread.currentThread().getContextClassLoader();
        try {
            element = parser.parse(jsonText);
        } catch (JsonSyntaxException e) {
            // 不是 json 串，就按普通的字符串来处理。
            element = new JsonPrimitive(jsonText);
        }
    }

    public JsonConfigElement(JsonElement element, ClassLoader classLoader) {
        super();
        this.element = element;
        this.classLoader = classLoader;
    }

    @Override
    public int size() {
        if (element == null) {
            return 0;
        }
        if (!element.isJsonArray()) {
            return 0;
        }
        return element.getAsJsonArray().size();
    }

    @Override
    public String attr(String attrName) {
        JsonObject jsonObject = asJsonObject(element);
        if (jsonObject == null) {
            return null;
        }
        JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(attrName);
        if (jsonPrimitive == null) {
            return null;
        }
        return jsonPrimitive.getAsString();
    }

    private JsonPrimitive asJsonPrimitive(JsonElement element) {
        if (element == null) {
            return null;
        }
        if (!element.isJsonPrimitive()) {
            return null;
        }
        JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
        return jsonPrimitive;
    }

    @Override
    public ConfigElement[] getChildren() {
        JsonArray jsonArray = asJsonArray(element);
        return toConfigElements(jsonArray);
    }

    private ConfigElement[] toConfigElements(JsonArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        if (jsonArray.size() == 0) {
            return new JsonConfigElement[0];
        }
        int size = jsonArray.size();
        ConfigElement[] children = new JsonConfigElement[size];
        for (int i = 0; i < size; i++) {
            JsonElement child = jsonArray.get(i);
            children[i] = new JsonConfigElement(child, classLoader);
        }
        return children;
    }

    private JsonArray asJsonArray(JsonElement element) {
        if (element == null) {
            return null;
        }
        if (!element.isJsonArray()) {
            return null;
        }
        JsonArray jsonArray = element.getAsJsonArray();
        return jsonArray;
    }

    private JsonObject asJsonObject(JsonElement element) {
        if (element == null) {
            return null;
        }
        if (!element.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = element.getAsJsonObject();
        if (jsonObject == null) {
            return null;
        }
        return jsonObject;
    }

    @Override
    public ConfigElement[] getChildren(String elementName) {
        JsonObject jsonObject = asJsonObject(element);
        if (jsonObject == null) {
            return null;
        }
        JsonElement child = jsonObject.get(elementName);
        JsonArray jsonArray = asJsonArray(child);
        return toConfigElements(jsonArray);
    }

    @Override
    public String getValue() {
        JsonPrimitive jsonPrimitive = asJsonPrimitive(element);
        if (jsonPrimitive == null) {
            return null;
        }
        return jsonPrimitive.getAsString();
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public <T> T asObject(Class<T> clazz) throws BusinessException {
        if (element == null) {
            return null;
        }
        return gson.fromJson(element, clazz);
    }

    @Override
    public ConfigElement getChild(String eleName) throws BusinessException {
        JsonObject jsonObject = asJsonObject(element);
        if (jsonObject == null) {
            return null;
        }
        JsonElement child = jsonObject.get(eleName);
        if (child == null) {
            return null;
        }
        return new JsonConfigElement(child, classLoader);
    }

    @Override
    public String getName() {
        // 对于 JSON 中的元素，本来就没有名字。
        return null;
    }

    @Override
    public Set<String> attrSet() {
        JsonObject jsonObject = asJsonObject(element);
        if (jsonObject == null) {
            return null;
        }
        Set<String> keys = new HashSet<>();
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        Iterator<Map.Entry<String, JsonElement>> it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry<String, JsonElement> entry = it.next();
            if (entry == null) {
                continue;
            }
            String key = entry.getKey();
            keys.add(key);
        }
        return keys;
    }

    @Override
    public String asText() {
        if (element == null) {
            return null;
        }
        return gson.toJson(element);
    }

}
