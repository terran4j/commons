package com.terran4j.commons.api2page.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableColumn {

    String name() default "";

    int width() default 10;

    boolean orderable() default false;
}
