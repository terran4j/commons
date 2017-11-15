package com.terran4j.demo.restpack;

import com.terran4j.commons.restpack.ExceptionHolder;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Order(100)
@Component
public class RestPackDemoAspect {

    private static final Logger log = LoggerFactory.getLogger(RestPackDemoAspect.class);

    public RestPackDemoAspect() {
        super();
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void httpDemoPackAspect() {
    }

    @After("httpDemoPackAspect()")
    public void doAfter(JoinPoint point) {
    }

    @Before("httpDemoPackAspect()")
    public void doBefore(JoinPoint point) throws BusinessException {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servlet = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest httpRequest = servlet.getRequest();
        String f = httpRequest.getParameter("f");
        if ("e".equals(f)) {
            throw new BusinessException(ErrorCodes.ACCESS_DENY)
                    .setMessage("不允许执行的操作!");
        }
    }

//    @AfterThrowing(pointcut = "httpDemoPackAspect()", throwing = "e")
//    public void handleThrowing(Exception e) {
//        if (log.isInfoEnabled()) {
//            log.info("handle throwed exception[{}]: {}", e.getClass().getName(), e.getMessage());
//        }
//        ExceptionHolder.set(e);
//    }

}
