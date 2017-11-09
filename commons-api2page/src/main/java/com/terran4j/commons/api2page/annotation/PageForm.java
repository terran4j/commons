package com.terran4j.commons.api2page.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PageForm {

    String value() default "";

    String title();

    boolean notInvoke() default false;

    Button[] buttons() default {};

}
