package com.terran4j.commons.api2page.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PageList {

    String value() default "";

    String title();

    boolean selectable() default false;

    String[] columns() default {};

    Button[] tableButtons() default {};

    Button[] rowButtons() default {};

}
