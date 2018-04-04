package com.terran4j.commons.restpack;

import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 本切面拦截有<code>@RequestMapping</code>注解，并且类上有<code>HttpResultPackController</code>注解的方法：<br>
 * 1. 生成并记录 requestId.<br>
 * 2. 记录开始时间。<br>
 * 3. 记录异常对象。
 *
 * @author jiangwei
 */
@Aspect
@Order(1)
@Component
public class RestPackAspect {

    private static final ThreadLocal<String> bufferRequestId = new ThreadLocal<>();

    private static final ThreadLocal<Long> bufferBeginTime = new ThreadLocal<>();

    private static final ThreadLocal<Logger> bufferLog = new ThreadLocal<>();

    public static final boolean isRestPack() {
        return bufferRequestId.get() != null;
    }

    public static final String getRequestId() {
        return bufferRequestId.get();
    }

    public static final Long getBeginTime() {
        return bufferBeginTime.get();
    }

    public static final Logger getLog() {
        return bufferLog.get();
    }

    public RestPackAspect() {
        super();
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void doRequestMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void doGetMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void doPostMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void doPutMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void doDeleteMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void doPatchMapping() {
    }

    private static final String POINTCUT_EXP = "doRequestMapping() " +
            "|| doGetMapping() || doPostMapping() || doPutMapping()" +
            "|| doDeleteMapping() || doPatchMapping()";

    @After(POINTCUT_EXP)
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
    @Before(POINTCUT_EXP)
    public void doBefore(JoinPoint point) {
        ServletRequestAttributes servlet = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servlet.getRequest();
        String requestId = generateRequestId();
        String requestPath = request.getRequestURI();
        MDC.put("requestId", requestId);
        MDC.put("requestPath", requestPath);

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

            Logger log = LoggerFactory.getLogger(clazz);
            bufferLog.set(log);
        }

        Logger log = bufferLog.get();
        if (log != null && log.isInfoEnabled()) {
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
            log.info("request '{}' begin, params:\n{}",
                    requestPath, Strings.toString(params));
        }
    }

    /**
     * 记录异常对象，以便于后续处理转化成<code>HttpResult</code>对象。
     * @param e 异常对象
     */
    @AfterThrowing(pointcut = POINTCUT_EXP, throwing = "e")
    public void handleThrowing(Exception e) {
        Logger log = bufferLog.get();
        if (log != null && log.isInfoEnabled()) {
            log.info("handle throw exception[{}]: {}",
                    e.getClass().getName(), e.getMessage());
        }
        ExceptionHolder.set(e);
    }

    public static String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    static void clearThreadLocal() {
        bufferRequestId.remove();
        bufferBeginTime.remove();
        ExceptionHolder.remove();
        bufferLog.remove();
    }
}
