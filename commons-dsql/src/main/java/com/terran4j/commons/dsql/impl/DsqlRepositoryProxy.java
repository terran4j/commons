package com.terran4j.commons.dsql.impl;

import com.terran4j.commons.dsql.DsqlExecutor;
import com.terran4j.commons.dsql.DsqlModifying;
import com.terran4j.commons.dsql.DsqlQuery;
import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;

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
        return (T) enhancer.create();
    }

    private Class<?> elementType;

    private final Class<?> proxyInterface;

    private final LocalVariableTableParameterNameDiscoverer paramNameDiscoverer
            = new LocalVariableTableParameterNameDiscoverer();

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
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type genericType = parameterizedType.getActualTypeArguments()[0];
        if (genericType instanceof Class<?>) {
            return (Class<?>) genericType;
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

        DsqlQuery query = method.getAnnotation(DsqlQuery.class);
        if (query != null) {
            String sqlName = query.value();
            if (StringUtils.isEmpty(sqlName)) {
                sqlName = method.getName();
            }
            return doQuery(sqlName, context, method, executor);
        }

        DsqlModifying modifying = method.getAnnotation(DsqlModifying.class);
        if (modifying != null) {
            String sqlName = modifying.value();
            if (StringUtils.isEmpty(sqlName)) {
                sqlName = method.getName();
            }
            return doModifying(sqlName, context, method, executor);
        }

        String methodName = method.getName();
        if (methodName.startsWith("query") || methodName.startsWith("get")
                || methodName.startsWith("list") || methodName.startsWith("find")
                || methodName.startsWith("select") || methodName.startsWith("read")
                || methodName.startsWith("count") || methodName.startsWith("load")) {
            return doQuery(methodName, context, method, executor);
        }

        if (methodName.startsWith("update") || methodName.startsWith("delete")
                || methodName.startsWith("remove") || methodName.startsWith("edit")
                || methodName.startsWith("write") || methodName.startsWith("modify")
                || methodName.startsWith("change") || methodName.startsWith("create")
                || methodName.startsWith("set") || methodName.startsWith("alter")) {
            return doModifying(methodName, context, method, executor);
        }

        String msg = "在接口 %s 中的 %s 方法状态不正确：\n" +
                "要么方法上用 @DsqlQuery 或 @DsqlModifying 注解修饰；\n" +
                "要么方法名用约定的单词开头：\n" +
                "如果是查询类操作，用 query,get,list,find,select,read,count,load 开头；\n" +
                "如果是修改/删除类操作，用 update,delete,remove,edit,write," +
                "modify,change,create,set,alter 开头。";
        throw new IllegalStateException(msg);
    }

    private Object doModifying(String sqlName, Map<String, Object> args,
                               Method method, DsqlExecutor executor) throws BusinessException {
        SqlInfo sqlInfo = SqlInfo.create(args, proxyInterface, sqlName);

        Class<?> returnType = method.getReturnType();
        if (returnType.equals(Integer.class) || returnType.equals(int.class)) {
            return executor.update(sqlInfo);
        }
        if (returnType.equals(Long.class) || returnType.equals(long.class)) {
            return (long) executor.update(sqlInfo);
        }

        throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                .put("method", method).put("returnType", returnType)
                .setMessage("Unknown returnType: ${returnType}, " +
                        "@DsqlModifying method ONLY support  returnTypes: " +
                        "Long, long, Integer, int");
    }

    private Object doQuery(String sqlName, Map<String, Object> args,
                           Method method, DsqlExecutor executor) throws BusinessException {
        SqlInfo sqlInfo = SqlInfo.create(args, proxyInterface, sqlName);

        Class<?> returnType = method.getReturnType();
        if (returnType.equals(Integer.class) || returnType.equals(int.class)) {
            return executor.query4Count(sqlInfo);
        }
        if (returnType.equals(Long.class) || returnType.equals(long.class)) {
            return executor.query4Count(sqlInfo);
        }
        if (returnType.equals(elementType)) {
            List<?> result = executor.query4List(sqlInfo, elementType);
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
            List<?> result = executor.query4List(sqlInfo, elementType);
            return result;
        }
        throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                .put("method", method).put("returnType", returnType)
                .put("elementType", elementType.getSimpleName())
                .setMessage("Unknown returnType: ${returnType}, " +
                        "@DsqlQuery method ONLY support  returnTypes: " +
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
        }

        String[] paramNames = paramNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            String key = param.getName();

            // 利用 Spring 提供的工具，获取参数名。
            if (paramNames != null && paramNames.length >= i + 1) {
                key = paramNames[i];
            }

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
