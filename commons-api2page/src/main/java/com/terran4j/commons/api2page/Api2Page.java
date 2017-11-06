package com.terran4j.commons.api2page;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Api2Page {

    String id();

    PageType type();

}
