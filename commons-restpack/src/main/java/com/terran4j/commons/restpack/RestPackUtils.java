package com.terran4j.commons.restpack;

import com.terran4j.commons.util.Beans;
import com.terran4j.commons.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RestPackUtils {

    private static final Logger log = LoggerFactory.getLogger(RestPackUtils.class);

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

    private static Set<Class<?>> javaClasses = new HashSet<Class<?>>();

    static {
        javaClasses.add(Boolean.class);
        javaClasses.add(Byte.class);
        javaClasses.add(Character.class);
        javaClasses.add(Short.class);
        javaClasses.add(Integer.class);
        javaClasses.add(Long.class);
        javaClasses.add(Float.class);
        javaClasses.add(Double.class);
        javaClasses.add(String.class);
        javaClasses.add(Date.class);
        javaClasses.add(Class.class);
    }

    public static final boolean isBasicType(Class<?> clazz) {
        return basicClasses.contains(clazz);
    }

    public static final boolean isJavaType(Class<?> clazz) {
        return javaClasses.contains(clazz);
    }

    public static void clearIgnoreFields(Object bean) {
        if (bean == null) {
            return;
        }

        Class<?> beanClass = bean.getClass();
        if (isJavaType(beanClass)) {
            return;
        }

        // 对于集合类型，要处理每一个子元素对象。
        if (bean instanceof Collection) {
            Collection collection = (Collection) bean;
            for (Object element : collection) {
                clearIgnoreFields(element);
            }
            return;
        }

        // 对于 Map 类型，要处理每一个值对象。
        if (bean instanceof Map) {
            Map map = (Map) bean;
            clearIgnoreFields(map.values());
            return;
        }

        // 处理每个属性字段。
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(beanClass);
        if (propertyDescriptors == null || propertyDescriptors.length == 0) {
            return;
        }
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String fieldName = propertyDescriptor.getName();

            // 必须满足 JavaBean 规范,要有 setter / getter 方法。
            Method getMethod = propertyDescriptor.getReadMethod();
            Method setMethod = propertyDescriptor.getWriteMethod();
            if (getMethod == null || setMethod == null) {
                continue;
            }

            Field field = null;
            try {
                field = beanClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // ignore it.
            }
            if (field == null) {
                if (log.isInfoEnabled()) {
                    log.info("在类 {} 中找不到字段：{} ", beanClass.getName(), fieldName);
                }
                continue;
            }

            RestPackIgnore restPackIgnore = field.getAnnotation(RestPackIgnore.class);
            if (restPackIgnore != null) {
                Class<?> fieldType = field.getType();
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
            } else {
                Object fieldValue = null;
                try {
                    fieldValue = getMethod.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    String msg = String.format("读取字段[ %s#%s ]的值出错: %s",
                            beanClass.getName(), fieldName, e.getMessage());
                    throw new RuntimeException(msg);
                }
                clearIgnoreFields(fieldValue);
            }
        }
    }

}
