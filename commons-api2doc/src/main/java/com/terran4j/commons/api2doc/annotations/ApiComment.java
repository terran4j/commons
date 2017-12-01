package com.terran4j.commons.api2doc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ //
		ElementType.TYPE, //
		ElementType.METHOD, //
		ElementType.PARAMETER, //
		ElementType.FIELD, //
})
public @interface ApiComment {

	String value() default "";

	String sample() default "";

	Class<?> see() default Object.class;

}
