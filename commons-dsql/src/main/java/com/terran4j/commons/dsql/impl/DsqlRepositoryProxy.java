package com.terran4j.commons.dsql.impl;

import com.terran4j.commons.dsql.DsqlExecutor;
import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.Query;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.data.repository.query.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.terran4j.commons.dsql.impl.DsqlRepositoryConfigRegistrar.getDsqlExecutor;

public class DsqlRepositoryProxy implements MethodInterceptor {

    public static final <T> T createProxyObject(Class<T> clazz) {
        // 增强器，动态代码生成器
        Enhancer enhancer = new Enhancer();

        // 回调方法
        DsqlRepositoryProxy proxy = new DsqlRepositoryProxy(clazz);
        enhancer.setCallback(proxy);

        // 设置生成类的父类类型
        enhancer.setSuperclass(clazz);
        // 动态生成字节码并返回代理对象
        return (T)enhancer.create();
    }

    private Class<?> elementType;

    private final Class<?> proxyInterface;

    private DsqlRepositoryProxy(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
        this.elementType = getElementType(proxyInterface);
    }

    public Class<?> getElementType(Class<?> proxyInterface) {
        Type[] types = proxyInterface.getGenericInterfaces();
        if (types == null || types.length != 1) {
            throw new RuntimeException(proxyInterface + " must implement ONLY ONE interface: " +
                    DsqlRepository.class);
        }
        Type type = types[0];
        ParameterizedType parameterizedType = (ParameterizedType)type;
        Type genericType = parameterizedType.getActualTypeArguments()[0];
        if (genericType instanceof Class<?>) {
            return (Class<?>)genericType;
        } else {
            throw new RuntimeException(proxyInterface + "'s genericType is NOT a class: " +
                    genericType);
        }
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects,
                            MethodProxy methodProxy) throws Throwable {

        DsqlExecutor executor = getDsqlExecutor();
        if (executor == null) {
            throw new IllegalStateException("DsqlExecutor is NOT registed in Application.");
        }

        Map<String, Object> context = getContext(method, objects);

        Query queryAnnotation = method.getAnnotation(Query.class);
        if (queryAnnotation == null) {
            throw new IllegalStateException(method + " MUST has @Query annotation.");
        }
        String sqlName = queryAnnotation.value();

        Class<?> returnType = method.getReturnType();
        if (returnType.equals(Integer.class) || returnType.equals(int.class)) {
            return executor.query4Count(context, proxyInterface, sqlName);
        }
        if (returnType.equals(Long.class) || returnType.equals(long.class)) {
            return executor.query4Count(context, proxyInterface, sqlName);
        }
        if (returnType.equals(elementType)) {
            List<?> result = executor.query4List(context, elementType, proxyInterface, sqlName);
            if (result == null || result.size() == 0) {
                return null;
            }
            if (result.size() > 1) {
                throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                        .put("resultsCount", result.size())
                        .setMessage("Too many Results, expect no more than 1 " +
                                "but has ${count}");
            }
            return result.get(0);
        }
        if (List.class.equals(returnType)) {
            List<?> result = executor.query4List(context, elementType, proxyInterface, sqlName);
            return result;
        }
        throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                .put("method", method).put("returnType", returnType)
                .put("elementType", elementType.getSimpleName())
                .setMessage("Unknown returnType: ${returnType}, " +
                        "ONLY support the following Types:\n" +
                        "List<${elementType}>, ${elementType}, " +
                        "Long, long, Integer, int.");
    }

    private Map<String, Object> getContext(Method method, Object[] args) throws BusinessException {
        Map<String, Object> context = new HashMap<>();

        // 没有参数的情况。
        Parameter[] params = method.getParameters();
        if (params == null || params.length == 0) {
            return context;
        }

        // 只有一个参数，并且没有 @Param 注解时，用 query 作默认的 key.
        if (params.length == 1 && params[0].getAnnotation(Param.class) == null) {
            if (args[0] != null) {
                context.put("args", args[0]);
            }
            return context;
        }

        for(int i = 0 ; i < params.length; i++) {
            Parameter param = params[i];
            String key = param.getName();

            Param paramAnnotation = param.getAnnotation(Param.class);
            if (paramAnnotation != null) {
                key = paramAnnotation.value();
            }

            Object value = args[i];
            if (value != null) {
                context.put(key, value);
            }
        }

        return context;
    }

}
