package com.terran4j.commons.api2page.annotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@java.lang.annotation.Target({})
public @interface Button {

    String name();

    String invoke() default "";

    ActionType actionType() default ActionType.auto;
}
