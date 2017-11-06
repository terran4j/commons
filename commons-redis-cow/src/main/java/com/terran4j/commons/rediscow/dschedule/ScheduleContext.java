package com.terran4j.commons.rediscow.dschedule;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标记在非静态的属性上，表示这个属性在调度中维持一个上下文变量。<br>
 * 在执行调度后，此属性的值会保存在持久层（数据库、Redis之类的，看具体实现）。<br>
 * 在执行调度前，从持久层加载上下文数据，并用反射的方式，赋值到此属性上。
 * 
 * @author wei.jiang
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ScheduleContext {

	String value() default "";
	
}
