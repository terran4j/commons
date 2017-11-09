package com.terran4j.commons.api2page.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Widget
public @interface WidgetInput {

    int line() default 1;

}