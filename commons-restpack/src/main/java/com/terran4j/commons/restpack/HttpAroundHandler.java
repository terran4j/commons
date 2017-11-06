package com.terran4j.commons.restpack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 
 * @author wei.jiang
 */
public interface HttpAroundHandler {

	/**
	 * 此处理器的优先级，越小优先级越高。<br>
	 * 当执行 preHandle 方法时，优先级越高的越先执行。<br>
	 * 当执行 postHandle 方法时，优先级越高的越后执行，与 preHandle 时正好反过来了。<br>
	 * 
	 * @return
	 */
	int getPriority();

	/**
	 * 请求前预处理。
	 * 
	 * @param point
	 * @param request
	 * @param response
	 * @return 为 null 表示后续继续执行，为不 null 表示后续不再执行，返回的是这个结果。
	 */
	HttpResult preHandle(ProceedingJoinPoint point, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 请求后后处理。
	 * 
	 * @param point
	 * @param request
	 * @param response
	 * @param result
	 * @return 为 null 表示后续继续执行，为不 null 表示后续不再执行，返回的是这个结果。
	 */
	HttpResult postHandle(ProceedingJoinPoint point, HttpServletRequest request, HttpServletResponse response,
			HttpResult result);

}