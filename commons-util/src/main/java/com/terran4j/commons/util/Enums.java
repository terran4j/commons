package com.terran4j.commons.util;

import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class Enums {

    public static Object getEnumObject(Class<?> enumType, String name) {
        if (!enumType.isEnum()) {
            return null;
        }
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Object enumArray = null;
        try {
            Method method = enumType.getMethod("values");
            enumArray = method.invoke(null,null);
            if (enumArray == null || !enumArray.getClass().isArray()) {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int size = Array.getLength(enumArray);
        if (size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            Enum enumObject = (Enum)Array.get(enumArray, i);
            if (enumObject.name().equals(name)) {
                return enumObject;
            }
        }
        return null;
    }

}
