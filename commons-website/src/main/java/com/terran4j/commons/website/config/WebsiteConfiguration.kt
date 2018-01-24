package com.terran4j.commons.website.config

import com.terran4j.commons.website.controller.WelcomeController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * website 模块的配置类。
 * @author terran4j
 */
@Configuration
open class WebsiteConfiguration : WebMvcConfigurerAdapter() {

    @Bean
    open fun welcomeController(): WelcomeController {
        return WelcomeController()
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
        /**
         * 一旦启用了 @EnableWebMvc，则 Spring Boot 中内置的 MVC 规则就不好使了。
         * 因此这里手工添加静态资源映射关系。
         */
        registry!!.addResourceHandler(PATH_PATTERN).addResourceLocations(PATH_LOCATION)
        super.addResourceHandlers(registry)
    }

    companion object {

        private const val PATH_PATTERN = "/**"

        private const val PATH_LOCATION = "classpath:/static/"
    }

}