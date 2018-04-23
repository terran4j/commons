/**
 * 
 */
package com.terran4j.commons.restpack.log;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 自动输出日志信息。
 * @author wei.jiang
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Log {
	
	String value() default "";

}


