package com.terran4j.commons.restpack;

import javax.servlet.http.HttpServletRequest;

import com.terran4j.commons.util.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

@ControllerAdvice
public class HttpErrorHandler {
	
	private static final Logger log = LoggerFactory.getLogger(HttpErrorHandler.class);

	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public HttpResult handleAllException(Exception e, HttpServletRequest request) {
		log.error("[Handled] unknown exception", e);
		return HttpResult.fail(new BusinessException(CommonErrorCode.UNKNOWN_ERROR, e));
	}

	@ResponseBody
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.OK)
	public HttpResult handleMissingServletRequestParameterException(
	        MissingServletRequestParameterException e, HttpServletRequest request) {
		if (log.isInfoEnabled()) {
			log.info("missing param: {} in path: {}",
                    e.getParameterName(), request.getPathInfo());
		}
		return HttpResult.fail(new BusinessException(ErrorCodes.NULL_PARAM)
                .put("key", e.getParameterName())
                .setMessage("参数 ${key} 不能为空！"));
	}

}
