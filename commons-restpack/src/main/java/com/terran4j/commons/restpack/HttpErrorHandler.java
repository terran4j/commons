package com.terran4j.commons.restpack;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;
import com.terran4j.commons.util.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class HttpErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpErrorHandler.class);

    @Autowired
    private HttpResultMapper httpResultMapper;

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        return toHttpResult(e, request);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleAllException(Exception e, HttpServletRequest request) {
        return toHttpResult(e, request);
    }

    private Object toHttpResult(Exception e, HttpServletRequest request) {

        long t0 = System.currentTimeMillis();
        if (request == null) {
            ServletRequestAttributes servlet = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            request = servlet.getRequest();
        }

        String requestId = RestPackAspect.generateRequestId();
        String requestPath = request.getRequestURI();
        MDC.put("requestId", requestId);
        MDC.put("requestPath", requestPath);

        BusinessException be;
        if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException me = (MissingServletRequestParameterException) e;
            log.info("missing param: {} in path: {}", me.getParameterName(), request.getPathInfo());
            be = new BusinessException(ErrorCodes.NULL_PARAM)
                    .put("key", me.getParameterName())
                    .setMessage("参数 ${key} 不能为空！");
        } else {
            log.error("[Handled] unknown exception", e);
            be = new BusinessException(CommonErrorCode.UNKNOWN_ERROR, e)
                    .setMessage(e.getClass().getName() + ": " + e.getMessage());
        }

        HttpResult result = HttpResult.fail(be);
        result.setRequestId(requestId);
        long t = System.currentTimeMillis();
        result.setServerTime(t);
        result.setSpendTime(t - t0);
        return httpResultMapper.toMap(result);
    }

}
