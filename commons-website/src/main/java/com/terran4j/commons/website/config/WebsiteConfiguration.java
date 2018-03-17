package com.terran4j.commons.website.config;

import com.terran4j.commons.website.controller.WelcomeController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebsiteConfiguration extends WebMvcConfigurerAdapter {

    private static final String PATH_PATTERN = "/**";

    private static final String PATH_LOCATION = "classpath:/static/";

    @Bean
    public WelcomeController welcomeController() {
        return new WelcomeController();
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 一旦启用了 @EnableWebMvc，则 Spring Boot 中内置的 MVC 规则就不好使了。
         * 因此这里手工添加静态资源映射关系。
         */
        registry.addResourceHandler(PATH_PATTERN).addResourceLocations(PATH_LOCATION);
        super.addResourceHandlers(registry);
    }
}
