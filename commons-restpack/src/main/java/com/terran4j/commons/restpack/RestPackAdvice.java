package com.terran4j.commons.restpack;

import com.terran4j.commons.util.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

/**
 * 本类负责将原始返回值、或异常类包裹在 HttpResult 对象中。
 * 
 * @author jiangwei
 *
 */
@Component
@ControllerAdvice
public class RestPackAdvice implements ResponseBodyAdvice<Object> {

//	private static final Logger log = LoggerFactory.getLogger(RestPackAdvice.class);

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return RestPackAspect.isRestPack();
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {

		// 不是 RestPack 处理的请求，就不需要特殊处理。
		if (!RestPackAspect.isRestPack()) {
			return body;
		}
		
		HttpResult result = null;
		Exception e = ExceptionHolder.get();
		if (e != null) {
			BusinessException be = convert(e);
			result = HttpResult.fail(be);
		} else {
			// 将原始返回值包裹在 HttpResult 对象中。
			result = HttpResult.success();
			if (body != null) {
                RestPackUtils.clearIgnoreFields(body);
				result.setData(body);
			}
		}
		setHttpResult(result);

		Logger log = RestPackAspect.getLog();
		if (log != null && log.isInfoEnabled()) {
			log.info("request '{}' end, response:\n{}", MDC.get("requestPath"), result);
		}
		
		RestPackAspect.clearThreadLocal();

		return result;
	}
	

	void setHttpResult(HttpResult result) {
		String requestId = RestPackAspect.getRequestId();
		if (requestId != null) {
			result.setRequestId(requestId);
		}
		Long beginTime = RestPackAspect.getBeginTime();
		if (beginTime != null) {
			result.setServerTime(beginTime);
			long spendTime = System.currentTimeMillis() - beginTime;
			result.setSpendTime(spendTime);
		}
	}

	BusinessException convert(Throwable e) {
		if (e instanceof BusinessException) {
			return (BusinessException) e;
		}

		if (e instanceof MissingServletRequestParameterException) {
			MissingServletRequestParameterException me = (MissingServletRequestParameterException) e;
			String paramKey = me.getParameterName();
			String paramType = me.getParameterType();

            Logger log = RestPackAspect.getLog();
            if (log != null && log.isInfoEnabled()) {
				log.info("miss param, key = {}, type = {}", paramKey, paramType);
			}

			return new BusinessException(ErrorCodes.NULL_PARAM)
                    .put("key", paramKey);
		}

		// Error 没有办法拦截，这里只能日志记录异常信息。
		if (e instanceof Error) {
			e.printStackTrace();

            Logger log = RestPackAspect.getLog();
            if (log != null) {
                log.error(e.getMessage(), e);
            }
		}

		return new BusinessException(CommonErrorCode.UNKNOWN_ERROR, e);
	}

}
