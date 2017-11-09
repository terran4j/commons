package com.terran4j.commons.api2page.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Widget
public @interface WidgetImage {

    int height() default 1;

}
