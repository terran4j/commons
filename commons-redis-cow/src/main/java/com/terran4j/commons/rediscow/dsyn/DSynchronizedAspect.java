package com.terran4j.commons.rediscow.dsyn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

import com.terran4j.commons.rediscow.cache.CacheService;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;

@Aspect
@Component
public class DSynchronizedAspect {

	private static Logger log = LoggerFactory.getLogger(DSynchronizedAspect.class);

	private static final String instanceId = UUID.randomUUID().toString();
	
	private static final ExpressionParser parser = new SpelExpressionParser();
	
	private static final Map<String, Expression> expressions = new ConcurrentHashMap<>();
	
	public static final Expression getExpression(String expEL) {
		Expression exp = expressions.get(expEL);
		if (exp != null) {
			return exp;
		}
		synchronized(DSynchronizedAspect.class) {
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
	private CacheService cacheService;

	@PostConstruct
	public void init() {
		if (log.isInfoEnabled()) {
			log.info("create DSynchronizedAspect Object.");
		}
	}

	@Pointcut("@annotation(com.terran4j.commons.rediscow.dsyn.DSynchronized)")
	public void distributedSynchronized() {
	}

	@Around("distributedSynchronized()")
	public Object doDistributedSynchronized(ProceedingJoinPoint point) throws Throwable {

		Object targetObject = point.getTarget();
		Class<?> targetClass = Classes.getTargetClass(targetObject);
		String className = targetClass.getName();
		String methodName = point.getSignature().getName();
		final Logger log = LoggerFactory.getLogger(targetClass);
		if (log.isInfoEnabled()) {
			log.info("doDistributedSynchronized, methodName: {}", methodName);
		}

		Object[] args = point.getArgs();
		Method method = Classes.getMethod(targetClass, methodName, args, DSynchronized.class);
		if (method == null) {
			log.error("method not found, className = {}, methodName = {}", className, methodName);
			return point.proceed(args);
		}
		DSynchronized dsyn = method.getAnnotation(DSynchronized.class);
		if (dsyn == null) {
			log.error("@DSynchronized not found, className = {}, methodName = {}", className, methodName);
			return point.proceed(args);
		}

		String keyEL = dsyn.value();
		String lockKey = getLockKey(keyEL, targetObject, method, args);
		long keepAlive = dsyn.keepAlive();
		Long expiredMillisecond = keepAlive > 0 ? keepAlive : null;
		long t0 = System.currentTimeMillis();
		long timeout = dsyn.timeout();
		long sleepTime = 0;
		String threadName = Thread.currentThread().getName();
		while (timeout > 0 && (System.currentTimeMillis() - t0 < timeout)) {
			if (log.isInfoEnabled()) {
				log.info("Thread[ {} ] prepare to get the lock: {}", threadName, lockKey);
			}
			boolean locked = cacheService.setObjectIfAbsent(lockKey, instanceId, expiredMillisecond);
			if (locked) {
				try {
					if (log.isInfoEnabled()) {
						log.info("Thread[ {} ] got the lock: {}, expired = {}", threadName, lockKey,
								expiredMillisecond);
					}
					return point.proceed();
				} finally {
					if (log.isInfoEnabled()) {
						log.info("Thread[ {} ] will remove the lock: {}", threadName, lockKey);
					}
					cacheService.remove(lockKey);
				}
			} else {
				if (log.isInfoEnabled()) {
					log.info("Thread[ {} ] can't get the lock: {}, will try later", threadName, lockKey);
				}
				// 暂时使用轮循的办法去尝试获取锁。
				Thread.sleep(sleepTime);
				if (sleepTime < 50) {
					sleepTime += 1; // 如果获取锁的时间越长，等待间隔也需要越大。
				}
			}
		}

		// 等待超时了，抛出异常。
		String msg = String.format("Wait lock[%s] timeout %dms in @DSynchronized", lockKey, timeout);
		throw new InterruptedException(msg);
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
		String lockKey = null;
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
		Annotation[][] paramsAnnos = method.getParameterAnnotations();
		if (paramsAnnos != null && paramsAnnos.length > 0) {
			for (Annotation[] paramAnnos : paramsAnnos) {
				if (paramAnnos == null || paramAnnos.length == 0) {
					continue;
				}

				for (Annotation paramAnno : paramAnnos) {
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
