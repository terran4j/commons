package com.terran4j.commons.jfinger;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.terran4j.commons.jfinger.builtin.LogCommand;
import com.terran4j.commons.jfinger.impl.CommandLineApplicationListener;

/**
 * 标记本包下的 bean 自动配置。
 * 
 * @author wei.jiang
 */
@ComponentScan(basePackageClasses = { //
		CommandLineApplicationListener.class, //
		LogCommand.class //
})
@Configuration
public class JFingerConfiguration {

}
