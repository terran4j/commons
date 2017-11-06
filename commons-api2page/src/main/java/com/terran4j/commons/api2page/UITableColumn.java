package com.terran4j.commons.api2page;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UITableColumn {

    String name();

    int width();

    boolean orderable() default false;
}
