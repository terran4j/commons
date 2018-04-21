package com.terran4j.commons.restpack;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RestController;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@RestController
public @interface RestPackController {
}
