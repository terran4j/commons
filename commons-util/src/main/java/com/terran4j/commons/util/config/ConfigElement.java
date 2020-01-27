package com.terran4j.commons.util.config;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;
import com.terran4j.commons.util.value.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface ConfigElement extends ValueSource<String, String> {

    int size();

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

    default String attr(String attrKey, String defaultValue) {
        String value = attr(attrKey);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    default int attr(String attrKey, int defaultValue) {
        String attrValue = attr(attrKey);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return defaultValue;
        }
        return Integer.parseInt(attrValue);
    }

    default Integer attrAsInt(String attrKey) {
        String attrValue = attr(attrKey);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return null;
        }
        return Integer.parseInt(attrValue);
    }

    default Long attrAsLong(String attrKey) {
        String attrValue = attr(attrKey);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return null;
        }
        return Long.parseLong(attrValue);
    }

    default Double attrAsDouble(String attrKey) {
        String attrValue = attr(attrKey);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return null;
        }
        return Double.parseDouble(attrValue);
    }

    ConfigElement[] getChildren();

    ConfigElement[] getChildren(String elementName);

    String getValue();

    default boolean attr(String attrKey, boolean defaultValue) {
        String attrValue = attr(attrKey);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return defaultValue;
        }
        return Boolean.parseBoolean(attrValue);
    }

    default Boolean attrAsBoolean(String attrKey) {
        String attrValue = attr(attrKey);
        if (attrValue == null || attrValue.trim().length() == 0) {
            return null;
        }
        return Boolean.parseBoolean(attrValue);
    }

    ClassLoader getClassLoader();

    default <T> T attr(String attrName, Class<T> clazz) throws BusinessException {
        ConfigElement attrValue = getChild(attrName);
        if (attrValue == null) {
            return null;
        }
        return attrValue.asObject(clazz);
    }

    default <T> List<T> getChildren(String attrKey, Class<T> clazz) throws BusinessException {
        ConfigElement[] children = getChildren(attrKey);
        if (children == null) {
            return null;
        }

        List<T> result = new ArrayList<>();
        if (children.length == 0) {
            return result;
        }

        for (ConfigElement child : children) {
            T item = child.asObject(clazz);
            result.add(item);
        }

        return result;
    }

    <T> T asObject(Class<T> clazz) throws BusinessException;

    ConfigElement getChild(String eleName) throws BusinessException;

    default String getChildText(String eleName) throws BusinessException {
        ConfigElement c = getChild(eleName);
        if (c == null) {
            return null;
        }
        return c.getValue();
    }

    String getName();

    Set<String> attrSet();

    default Object createObject(String classAttrKey) throws BusinessException {
        String className = attr(classAttrKey);
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
