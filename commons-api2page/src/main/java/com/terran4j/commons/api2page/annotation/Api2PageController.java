package com.terran4j.commons.api2page.annotation;

import com.terran4j.commons.restpack.RestPackController;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@RestPackController
public @interface Api2PageController {

}
