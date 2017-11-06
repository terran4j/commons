package com.terran4j.commons.dsql;

import com.terran4j.commons.dsql.impl.DsqlRepositoryConfigRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DsqlRepositoryConfigRegistrar.class)
public @interface EnableDsqlRepositories {

    Class<?>[] value() default {};

    Class<?>[] basePackageClasses() default {};

}
