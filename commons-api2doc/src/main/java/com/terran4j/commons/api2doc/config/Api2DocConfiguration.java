package com.terran4j.commons.api2doc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.terran4j.commons.api2doc.codewriter.RetrofitCodeWriter;
import com.terran4j.commons.api2doc.controller.Api2DocController;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;

@ComponentScan(basePackageClasses = { // 
		Api2DocController.class, // 
		Api2DocCollector.class, // 
		RetrofitCodeWriter.class, //
})
@Configuration
public class Api2DocConfiguration {

}