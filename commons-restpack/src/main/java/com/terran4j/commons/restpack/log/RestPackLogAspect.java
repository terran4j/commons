package com.terran4j.commons.restpack.log;

import com.terran4j.commons.restpack.Log;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.Strings;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 对有 @Log 的方法调用进行拦截，以记录日志。
 *
 * @author wei.jiang
 */
@Aspect
@Order(100)
@Component
public class RestPackLogAspect {

    @Around("@annotation(com.terran4j.commons.restpack.Log)")
    public Object doLog(ProceedingJoinPoint point) throws Throwable {

        Object target = point.getTarget();
        Class<?> targetClass = Classes.getTargetClass(target);
        String methodName = point.getSignature().getName();
        Object[] args = point.getArgs();
        Method method = Classes.getMethod(targetClass, methodName, args, Log.class);

        // 没有 @Log 注释，让方法正常执行。
        if (method == null) {
            return point.proceed(args);
        }

        // 每个类的日志对象，slf4j 已经缓存了，这里不需要再缓存。
        Logger log = LoggerFactory.getLogger(targetClass);

        // 操作的名称，如果没有定义就用"类名::方法名"
        Log logAnno = method.getAnnotation(Log.class);
        String actionName = targetClass.getSimpleName() + "::" + methodName;
        if (logAnno != null && StringUtils.hasText(logAnno.value())) {
            actionName = logAnno.value();
        }

        // 记录一个方法调用的开始。
        if (log != null && log.isInfoEnabled()) {
            if (args != null && args.length > 0) {
                String argsText = Strings.toString(args);
                if (argsText.length() > 100) {
                    argsText = argsText.substring(0, 100) + "......";
                }
                log.info("{} begin, args = {}", actionName, argsText);
            } else {
                log.info("{} begin.", actionName);
            }
        }

        long beginTime = System.currentTimeMillis();
        Object response = point.proceed(args); // 执行服务
        long endTime = System.currentTimeMillis();
        long spendTime = endTime - beginTime; // 统计运行时间。

        // 记录一个方法调用的结束。
        if (log != null && log.isInfoEnabled()) {
            if (response != null) {
                String responseText = Strings.toString(response);
                if (responseText.length() > 100) {
                    responseText = responseText.substring(0, 100) + "......";
                }
                log.info("{} end, returnObject = {}", actionName, responseText);
            }
            log.info("{} end, spend {}ms", actionName, spendTime);
        }

        return response;
    }

}
