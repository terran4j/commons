package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDataType;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.Enums;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * 根据类上的 Api2Doc 注解信息，创建 JavaBean、数组、列表等对象。
 */
public class Api2DocObjectFactory {

    private static final Logger log = LoggerFactory.getLogger(Api2DocObjectFactory.class);

    public static <T> T createBean(Class<T> clazz) {
        Stack<Class<?>> classStack = new Stack<Class<?>>();
        return createBean(clazz, null, classStack);
    }

    public static <E> List<E> createList(Class<E> clazz, int size) {
        Stack<Class<?>> classStack = new Stack<Class<?>>();
        return doCreateList(clazz, size, classStack);
    }

    public static <T> T[] createArray(Class<T> clazz, int size) {
        Stack<Class<?>> classStack = new Stack<Class<?>>();
        return doCreateArray(clazz, size, classStack);
    }

    private static <E> List<E> doCreateList(Class<E> clazz, int size, Stack<Class<?>> classStack) {
        List<E> list = new ArrayList<>();

        if (classStack.contains(clazz)) {
            return list;
        }

        for (int i = 0; i < size; i++) {
            E element = createBean(clazz, null, classStack);
            list.add(element);
        }
        return list;
    }

    private static <T> T[] doCreateArray(Class<T> clazz, int size, Stack<Class<?>> classStack) {
        if (size < 0) {
            return null;
        }
        T[] array = (T[]) Array.newInstance(clazz, size);

        if (classStack.contains(clazz)) {
            return array;
        }

        for (int i = 0; i < size; i++) {
            Object element = createBean(clazz, null, classStack);
            Array.set(array, i, element);
        }
        return array;
    }


    /**
     * 如果是 JavaBean 类，就创建这个 JavaBean 对象；<br>
     * 如果是 List / Array 对象，则什么都不做；<br>
     * 如果是 简单类型 对象，就创建符合这个类型的值。
     *
     * @param clazz 数据对象的类型。
     * @return 填充后的数据对象。
     */
    private static <T> T createBean(Class<T> clazz, String defaultValue, Stack<Class<?>> classStack) {
        if (clazz == null) {
            return null;
        }

        final ApiDataType dataType = ApiDataType.toDataType(clazz);
        if (dataType == null) {
            log.warn("无法识别的类型, class = {}", clazz);
            return null;
        }

        if (dataType.isArrayType()) {
            if (log.isInfoEnabled()) {
                log.info("数组类型，createBean 不处理, class = {}", clazz);
            }
            return null;
        }

        if (dataType.isSimpleType()) {
            if (defaultValue == null) {
                defaultValue = dataType.getDefault();
            }
            Object result = dataType.parseValue(defaultValue);
            result = adaptSimpleType(result, clazz);
            return (T) result;
        }

        // 处理 JavaBean 对象的情况。
        if (dataType.isObjectType()) {

            T object = null;
            try {
                object = clazz.newInstance();
            } catch (InstantiationException e) {
                log.warn("不能根据类创建对象，class = {}, 原因：{}", clazz, e.getMessage());
                return null;
            } catch (IllegalAccessException e) {
                log.warn("不能根据类创建对象，class = {}, 原因：{}", clazz, e.getMessage());
                return null;
            }
            if (object == null) {
                log.warn("不能根据类创建对象，class = {}", clazz);
                return null;
            }

            // 获取 JavaBean 的属性。
            PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(clazz);
            if (props == null || props.length == 0) { // 没有属性就不用处理。
                return object;
            }

            // 之前有过此类的信息，不用再次输出。
            if (classStack.contains(clazz)) {
                return object;
            }

            // 有属性，设置属性值。
            classStack.push(clazz);
            for (PropertyDescriptor prop : props) {
                String fieldName = prop.getName();
                try {
                    fillField(fieldName, object, classStack);
                } catch (Exception e) {
                    log.warn("给字段设值出错， class = {}, fieldName = {}", clazz, fieldName);
                }
            }
            classStack.pop();

            return object;
        }

        log.warn("无法识别的类型，class = {}", clazz);
        return null;
    }

    private static void fillField(String name, Object bean, Stack<Class<?>> classStack) {
        Class<?> clazz = bean.getClass();

        PropertyDescriptor fieldProp = null;
        try {
            fieldProp = PropertyUtils.getPropertyDescriptor(bean, name);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        if (fieldProp == null) {
            throw new RuntimeException("field[" + name + "] NOT found in class: " + clazz);
        }
        if (Api2DocUtils.isFilter(fieldProp, clazz)) {
            return;
        }

        String fieldName = fieldProp.getName();

        Method getMethod = fieldProp.getReadMethod();
        if (getMethod == null) {
            log.warn("没有 getter 方法, class = {}, fieldName = {}", clazz, fieldName);
            return;
        }

        Method setMethod = fieldProp.getWriteMethod();
        if (setMethod == null) {
            log.warn("没有 setter 方法, class = {}, fieldName = {}", clazz, fieldName);
            return;
        }

        Class<?> fieldClass = getMethod.getReturnType();
        Field field = Classes.getField(fieldName, clazz);
        if (field == null) {
            log.warn("找不到字段定义， class = {}, fieldName = {}", clazz, fieldName);
            return;
        }

        ApiDataType fieldDataType = ApiDataType.toDataType(fieldClass);
        if (fieldDataType == null) {
            log.warn("未知字段类型");
            return;
        }

        Object fieldValue = null;
        if (fieldDataType.isSimpleType()) {

            ApiComment apiComment = field.getAnnotation(ApiComment.class);
            String defaultValue = ApiCommentUtils.getSample(apiComment, fieldName);
            fieldValue = createBean(fieldClass, defaultValue, classStack);

            Class<?> paramType = setMethod.getParameterTypes()[0];
            fieldValue = adaptSimpleType(fieldValue, paramType);
        } else if (fieldDataType.isObjectType()) {
            fieldValue = createBean(fieldClass, null, classStack);
        } else if (fieldDataType.isArrayType()) {
            int size = 1;
            ApiComment apiComment = field.getAnnotation(ApiComment.class);
            if (apiComment != null) {
                String sizeText = ApiCommentUtils.getSample(apiComment, field.getName());
                if (StringUtils.hasText(sizeText)) {
                    try {
                        size = Integer.parseInt(sizeText);
                    } catch (Exception e) {
                        log.warn("List 或 Array 类型的字段上，@ApiComment 注解的 sample 属性应该是数字" +
                                "（代表它在 mock 时元素的个数）, sample = {}", sizeText);
                    }
                }
            }
            Class<?> elementClass = getArrayElementClass(field);
            if (fieldClass.isArray()) {
                fieldValue = doCreateArray(elementClass, size, classStack);
            } else if (List.class.equals(fieldClass)) {
                fieldValue = doCreateList(elementClass, size, classStack);
            } else {
                log.warn("不支持的集合类型，目前只支持 Array OR List， class = {}, fieldName = {}" +
                        ", fieldClass = {}", clazz, fieldName, fieldClass);
            }
        }

        try {
            setMethod.invoke(bean, fieldValue);
        } catch (Exception e) {
            log.warn("调用 setter 方法失败, \n" +
                    "clazz = {}, \n" +
                    "setMethod = {}, \n" +
                    "fieldValue = {}, \n" +
                    "失败原因： {}", clazz, setMethod, fieldValue, e.getMessage());
        }
    }

    private static Object adaptSimpleType(Object sourceValue, Class<?> targetType) {
        if (Long.class.equals(targetType) || long.class.equals(targetType)) {
            return Long.parseLong(sourceValue.toString());
        }
        if (Byte.class.equals(targetType) || byte.class.equals(targetType)) {
            return Byte.parseByte(sourceValue.toString());
        }
        if (Short.class.equals(targetType) || short.class.equals(targetType)) {
            return Short.parseShort(sourceValue.toString());
        }
        if (Float.class.equals(targetType) || float.class.equals(targetType)) {
            return Float.parseFloat(sourceValue.toString());
        }

        if (targetType.isEnum()) {
            return Enums.getEnumObject(targetType, sourceValue.toString());
        }

        return sourceValue;
    }

    private static final Class<?> getArrayElementClass(Field field) {

        Class<?> returnType = field.getType();
        if (returnType.isArray()) {
            Class<?> elementClass = returnType.getComponentType();
            return elementClass;
        }

        if (Classes.isInterface(returnType, Collection.class)) {
            Type type = field.getGenericType();
            Type elementType = Api2DocUtils.getGenericType(type);
            if (elementType instanceof Class<?>) {
                Class<?> elementClass = (Class<?>) elementType;
                return elementClass;
            }
        }

        log.warn("不支持的数组类型");
        return null;
    }

}
