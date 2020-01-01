package com.terran4j.commons.util.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.terran4j.commons.util.error.BusinessException;

import java.util.Set;

public class JsonConfigElement implements ConfigElement {

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
    public String attr(String attrName) {
        return null;
    }

    @Override
    public ConfigElement[] getChildren() {
        return new ConfigElement[0];
    }

    @Override
    public ConfigElement[] getChildren(String elementName) {
        return new ConfigElement[0];
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public <T> T asObject(Class<T> clazz) throws BusinessException {
        return null;
    }

    @Override
    public ConfigElement getChild(String eleName) throws BusinessException {
        return null;
    }

    @Override
    public String getChildText(String eleName) throws BusinessException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<String> attrSet() {
        return null;
    }

    @Override
    public String asText() {
        return null;
    }
}
