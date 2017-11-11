package com.terran4j.commons.restpack;

import com.terran4j.commons.util.Beans;
import com.terran4j.commons.util.Classes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class RestPackUtils {

    private static Set<Class<?>> basicClasses = new HashSet<Class<?>>();

    static {
        basicClasses.add(boolean.class);
        basicClasses.add(byte.class);
        basicClasses.add(char.class);
        basicClasses.add(short.class);
        basicClasses.add(int.class);
        basicClasses.add(long.class);
        basicClasses.add(float.class);
        basicClasses.add(double.class);
    }

    public static final boolean isBasicType(Class<?> clazz) {
        return basicClasses.contains(clazz);
    }

    public static void clearIgnoreFields(Object bean) {
        if (bean == null) {
            return;
        }

        Class<?> beanClass = bean.getClass();
        Field[] ignoreFields = Classes.getFields(RestPackIgnore.class, beanClass);
        if (ignoreFields == null || ignoreFields.length == 0) {
            return;
        }

        for (Field ignoreField : ignoreFields) {
            String fieldName = ignoreField.getName();
            Class<?> fieldType = ignoreField.getType();
            if (isBasicType(fieldType)) {
                String msg = String.format("@%s 不允许修饰在基本类型字段上\n字段： %s %s \n类：%s",
                        RestPackIgnore.class.getSimpleName(), fieldType.getSimpleName(), fieldName,
                        beanClass.getName());
                throw new RuntimeException(msg);
            }
            try {
                Beans.setFieldValue(bean, fieldName, null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("清除 RestPackIgnore 字段值出错： " + e.getMessage(), e);
            }
        }
    }

}
