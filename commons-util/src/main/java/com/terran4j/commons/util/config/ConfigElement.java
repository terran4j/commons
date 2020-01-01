package com.terran4j.commons.util.config;

import com.google.gson.JsonElement;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;
import com.terran4j.commons.util.value.ValueSource;

import java.util.Set;

public interface ConfigElement extends ValueSource<String, String> {

    default String get(String key) {
        String[] array = key.split("\\.");
        ConfigElement current = this;
        for (String item : array) {
            ConfigElement element = null;
            try {
                element = current.getChild(item);
            } catch (BusinessException e) {
                return null;
            }
            if (element == null) {
                return null;
            }
            current = element;
        }
        return current.getValue();
    }

    String attr(String attrName);

    default String attr(String attriName, String defaultValue) {
        String value = attr(attriName);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    default int attr(String attriName, int defaultValue) {
        String attrValue = attr(attriName);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return defaultValue;
        }
        return Integer.parseInt(attrValue);
    }

    default Integer attrAsInt(String attriName) {
        String attrValue = attr(attriName);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return null;
        }
        return Integer.parseInt(attrValue);
    }

    ConfigElement[] getChildren();

    ConfigElement[] getChildren(String elementName);

    String getValue();

    default boolean attr(String attriName, boolean defaultValue) {
        String attrValue = attr(attriName);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(attrValue);
    }

    default Boolean attrAsBoolean(String attriName) {
        String attrValue = attr(attriName);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return null;
        }
        return Boolean.parseBoolean(attrValue);
    }

    ClassLoader getClassLoader();

    default <T> T attr(String attrName, Class<T> clazz) throws BusinessException {
        String attrValue = attr(attrName);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return null;
        }
        return (T) createObject(attrName);
    }

    <T> T asObject(Class<T> clazz) throws BusinessException;

    ConfigElement getChild(String eleName) throws BusinessException;

    String getChildText(String eleName) throws BusinessException;

    String getName();

    Set<String> attrSet();

    default Object createObject(String classAttriName) throws BusinessException {
        String className = attr(classAttriName);
        try {
            Class<?> clazz = getClassLoader().loadClass(className);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new BusinessException(CommonErrorCode.CONFIG_ERROR, e)
                    .put("className", className).put("element", asText());
        } catch (InstantiationException e) {
            throw new BusinessException(CommonErrorCode.CONFIG_ERROR, e)
                    .put("className", className).put("element", asText());
        } catch (IllegalAccessException e) {
            throw new BusinessException(CommonErrorCode.CONFIG_ERROR, e)
                    .put("className", className).put("element", asText());
        }
    }

    String asText();
}
