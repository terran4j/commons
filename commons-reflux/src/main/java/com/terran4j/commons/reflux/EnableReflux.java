package com.terran4j.commons.reflux;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.terran4j.commons.reflux.client.RefluxClientConfiguration;
import com.terran4j.commons.reflux.server.RefluxServerConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ //
		RefluxClientConfiguration.class, //
		RefluxServerConfiguration.class //
})
public @interface EnableReflux {

}
