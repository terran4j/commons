package com.terran4j.commons.jfinger;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({})
@Retention(RUNTIME)
public @interface CommandOption {

	/**
	 * 
	 * @return
	 */
	String key();

	String name() default "";

	String desc() default "";
	
	boolean required() default false;
	
	OptionType type() default OptionType.STRING;
}