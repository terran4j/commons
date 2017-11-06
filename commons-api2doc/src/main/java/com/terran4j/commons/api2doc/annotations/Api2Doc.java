package com.terran4j.commons.api2doc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface Api2Doc {
	
	int DEFAULT_ORDER = 100;

	/**
	 * 文档的id。<br>
	 * 对于一个类中的重载方法，一定要用不同的 id 区分，不然会出错。
	 * 
	 * @return
	 */
	@AliasFor("value")
	String id() default "";

	/**
	 * 文档的id。<br>
	 * 对于一个类中的重载方法，一定要用不同的 id 区分，不然会出错。
	 * 
	 * @return
	 */
	@AliasFor("id")
	String value() default "";

	/**
	 * 是否忽略此文档。
	 * 
	 * @return
	 */
	boolean ignore() default false;

	/**
	 * 设置文档的排序。<br>
	 * 数字越小，排序越靠前。
	 * 
	 * @return
	 */
	int order() default DEFAULT_ORDER;
	
	String name() default "";

}