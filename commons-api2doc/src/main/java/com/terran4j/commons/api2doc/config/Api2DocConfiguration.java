package com.terran4j.commons.api2doc.config;

import com.terran4j.commons.api2doc.codewriter.RetrofitCodeWriter;
import com.terran4j.commons.api2doc.controller.Api2DocController;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 可以通过配置 terran4j.api2doc.enabled 来启用或禁用文档服务。
 */
@ConditionalOnExpression("${terran4j.api2doc.enabled:true}")
@ComponentScan(basePackageClasses = {
        Api2DocController.class,
        Api2DocCollector.class,
        RetrofitCodeWriter.class,
})
@Configuration
public class Api2DocConfiguration {

}