package com.terran4j.commons.dsql.config;

import com.terran4j.commons.dsql.impl.DsqlExecutorImpl;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(basePackageClasses = DsqlExecutorImpl.class)
@Configuration
public class DsqlConfiguration {
}
