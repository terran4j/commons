package com.terran4j.commons.dsql;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DsqlQuery {

    String value();
}
