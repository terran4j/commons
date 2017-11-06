package com.terran4j.commons.jfinger;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	
	/**
	 * 命令组名称。
	 * @return
	 */
	String name() default "";
	
	/**
	 * 命令组描述。
	 * @return
	 */
	String desc() default "";
	
	/**
	 * 命令中的选项。
	 * @return
	 */
	CommandOption[] options() default {};
}
