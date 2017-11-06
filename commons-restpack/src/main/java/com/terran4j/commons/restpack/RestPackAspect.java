package com.terran4j.commons.restpack;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.Strings;

/**
 * 本切面拦截有<code>@RequestMapping</code>注解，并且类上有<code>HttpResultPackController</code>注解的方法：<br>
 * 1. 生成并记录 requestId.<br>
 * 2. 记录开始时间。<br>
 * 3. 记录异常对象。
 * 
 * @author jiangwei
 *
 */
@Aspect
@Order(1)
@Component
public class RestPackAspect {

	private static final Logger log = LoggerFactory.getLogger(RestPackAspect.class);

	private static final ThreadLocal<String> bufferRequestId = new ThreadLocal<>();

	private static final ThreadLocal<Long> bufferBeginTime = new ThreadLocal<>();

	private static final ThreadLocal<Exception> bufferException = new ThreadLocal<>();

	public static final boolean isRestPack() {
		return bufferRequestId.get() != null;
	}

	public static final String getRequestId() {
		return bufferRequestId.get();
	}

	public static final Long getBeginTime() {
		return bufferBeginTime.get();
	}

	public static final Exception getException() {
		return bufferException.get();
	}

	private final ObjectMapper objectMapper;

	public RestPackAspect(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
		if (log.isInfoEnabled()) {
			log.info("new RestPackAspect...");
		}
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void httpResultPackAspect() {
	}

	@After("httpResultPackAspect()")
	public void doAfter(JoinPoint point) {
		if (isRestPack()) {
			RequestAttributes servlet = RequestContextHolder.getRequestAttributes();
			HttpServletResponse response = ((ServletRequestAttributes) servlet).getResponse();
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Type", "text/json;charset=UTF-8");
		}
	}

	/**
	 * 记录请求日志、requestId、开始处理时间。
	 * 
	 * @param point
	 */
	@Before("httpResultPackAspect()")
	public void doBefore(JoinPoint point) {
		ServletRequestAttributes servlet = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = servlet.getRequest();
		String requestId = generateRequestId();
		String requestPath = request.getRequestURI();
		MDC.put("requestId", requestId);
		MDC.put("requestPath", requestPath);
		if (log.isInfoEnabled()) {
			Map<String, Object> params = new HashMap<>();
			Enumeration<String> it = request.getParameterNames();
			while (it.hasMoreElements()) {
				String key = it.nextElement();
				String value = request.getParameter(key);
				if (value != null) {
					params.put(key, value);
				}
				String[] values = request.getParameterValues(key);
				if (values != null && values.length > 1) {
					params.put(key, values);
				}
			}
			log.info("request '{}' begin, params:\n{}", requestPath, Strings.toString(params));
		}

		// 写入 ThreadLocal 数据之前，先清除以前的历史数据（如果有的话）。
		clearThreadLocal();

		// 只有类上或父类上有 @HttpResultPackController 注解的，才需要打包返回结果。
		Object target = point.getTarget();
		Class<?> clazz = Classes.getTargetClass(target);
		RestPackController pack = clazz.getAnnotation(RestPackController.class);
		if (pack != null) {
			long beginTime = System.currentTimeMillis();
			bufferBeginTime.set(beginTime);
			bufferRequestId.set(requestId);
		}
	}

	/**
	 * 记录异常对象，以便于后续处理转化成<code>HttpResult</code>对象。
	 * 
	 * @param e
	 *            异常对象
	 */
	@AfterThrowing(pointcut = "httpResultPackAspect()", throwing = "e")
	public void handleThrowing(Exception e) {
		if (log.isInfoEnabled()) {
			log.info("handle throwed exception[{}]: {}", e.getClass().getName(), e.getMessage());
		}
		bufferException.set(e);
	}

	String generateRequestId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	static void clearThreadLocal() {
		bufferRequestId.remove();
		bufferBeginTime.remove();
		bufferException.remove();
	}
}
