package com.terran4j.commons.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Objects {
    public static boolean isSame(Object o1, Object o2){
        if(o1 == null && o2 == null)return true;
        if(o1 == null || o2 == null)return false;
        return o1.equals(o2);

    }
    public static boolean isSame(Boolean o1, Boolean o2){
        if(o1 == null && o2 == null)return true;
        if(o1 == null || o2 == null)return false;
        return o1.booleanValue() == o2.booleanValue();
    }

    public static Object getField(Object object, String field) {
        try {
            Class<?> clazz = object.getClass();
            Method method = method = clazz.getMethod("get"+Character.toUpperCase(field.charAt(0))+field.substring(1));
            return method.invoke(object);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
