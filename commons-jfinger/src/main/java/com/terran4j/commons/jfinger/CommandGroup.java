package com.terran4j.commons.jfinger;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CommandGroup {

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
	 * 公共的命令行选项。
	 * @return
	 */
	CommandOption[] options() default {};
}
