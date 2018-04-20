package com.terran4j.commons.hedis.dsyn;

import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class DSynchronizedAspect {

    private static Logger log = LoggerFactory.getLogger(DSynchronizedAspect.class);

    private static final ExpressionParser parser = new SpelExpressionParser();

    private static final Map<String, Expression> expressions = new ConcurrentHashMap<>();

    public static final Expression getExpression(String expEL) {
        Expression exp = expressions.get(expEL);
        if (exp != null) {
            return exp;
        }
        synchronized (DSynchronizedAspect.class) {
            exp = expressions.get(expEL);
            if (exp != null) {
                return exp;
            }
            exp = parser.parseExpression(expEL);
            expressions.put(expEL, exp);
            return exp;
        }
    }

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        log.info("create DSynchronizedAspect Object.");
    }

    @Pointcut("@annotation(com.terran4j.commons.hedis.dsyn.DSynchronized)")
    public void distributedSynchronized() {
    }

    @Around("distributedSynchronized()")
    public Object doDistributedSynchronized(ProceedingJoinPoint point) throws Throwable {

        // 获取当前的要同步的方法。
        Object targetObject = point.getTarget();
        Class<?> targetClass = Classes.getTargetClass(targetObject);
        String className = targetClass.getName();
        String methodName = point.getSignature().getName();
        final Logger log = LoggerFactory.getLogger(targetClass);
        log.info("DSynchronized, methodName: {}", methodName);

        Object[] args = point.getArgs();
        Method method = Classes.getMethod(targetClass, methodName, args, DSynchronized.class);
        if (method == null) {
            log.error("method not found, className = {}, methodName = {}",
                    className, methodName);
            return point.proceed(args);
        }
        DSynchronized synAnno = method.getAnnotation(DSynchronized.class);
        if (synAnno == null) {
            log.error("@DSynchronized not found, className = {}, methodName = {}", className, methodName);
            return point.proceed(args);
        }

        // 计算出锁的 key。
        String keyEL = synAnno.value();
        String lockKey = getLockKey(keyEL, targetObject, method, args);

        long keepAlive = synAnno.keepAlive();
        if (keepAlive <= 100) {
            throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                    .setMessage("keepAlive 设置不能小于 100 毫秒。")
                    .put("className", className).put("methodName", methodName)
                    .put("keyEL", keyEL);
        }

        long timeout = synAnno.timeout();

        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        if (timeout > 0) { // 获取锁有超时时间限制。
            isLocked = lock.tryLock(timeout, keepAlive, TimeUnit.MILLISECONDS);
        } else if (timeout <= 0) { // 获取锁时无超时时间限制，获取不到时会一直等待。
            lock.lock(keepAlive, TimeUnit.MILLISECONDS);
            isLocked = true;
        }

        // 获取锁超时了，抛出异常。
        if (!isLocked) {
            String msg = String.format("DSynchronized, wait lock %s timeout %dms",
                    lockKey, timeout);
            throw new InterruptedException(msg);
        }

        try {
            // 获取到锁，执行服务。
            log.info("DSynchronized, lock = {}, keepAlive = {}", lockKey, keepAlive);
            return point.proceed();
        } finally {
            // 释放锁。
            log.info("DSynchronized, unlock = {}", lockKey);
            lock.unlock();
        }
    }

    public static String getLockKey(String keyEL, Object target, Method method, Object[] args)
            throws BusinessException {
        try {
            DSynchArgs.clear();
            return doGetLockKey(keyEL, target, method, args);
        } finally {
            DSynchArgs.clear();
        }
    }

    static String doGetLockKey(String keyEL, Object target, Method method, Object[] args) throws BusinessException {
        String lockKey;
        if (StringUtils.isEmpty(keyEL)) {
            lockKey = Classes.toIdentify(method);
        } else {
            String[] params = getNamesByAnno(method);
            Expression expression = getExpression(keyEL);
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable("target", target);

            int length = params.length;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    String key = params[i];
                    Object value = args[i];
                    if (StringUtils.isEmpty(key) || value == null) {
                        continue;
                    }
                    context.setVariable(key, value);
                    DSynchArgs.set(key, value);
                }
            }
            lockKey = expression.getValue(context, String.class);
            if (StringUtils.isEmpty(lockKey)) {
                throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                        .put("keyEL", keyEL).put("keyValue", lockKey)
                        .setMessage("@DSynchronized中定义锁表达式为空。");
            }
        }
        return lockKey;
    }

    public static String[] getNamesByAnno(Method method) {
        List<String> names = new ArrayList<>();
        Annotation[][] paramsAnnotations = method.getParameterAnnotations();
        if (paramsAnnotations != null && paramsAnnotations.length > 0) {
            for (Annotation[] paramAnnotations : paramsAnnotations) {
                if (paramAnnotations == null || paramAnnotations.length == 0) {
                    continue;
                }

                for (Annotation paramAnno : paramAnnotations) {
                    if (paramAnno instanceof Param) {
                        Param param = (Param) paramAnno;
                        String name = (param == null ? "" : param.value());
                        if (StringUtils.hasText(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
            }
        }
        return names.toArray(new String[names.size()]);
    }

}
