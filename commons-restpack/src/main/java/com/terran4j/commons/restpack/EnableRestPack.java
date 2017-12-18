package com.terran4j.commons.restpack;

import com.terran4j.commons.website.config.WebsiteConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.lang.annotation.*;

/**
 * 启用 RestPack 服务。
 *
 * @author jiangwei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableWebMvc
@Import({RestPackConfiguration.class, WebsiteConfiguration.class})
public @interface EnableRestPack {

}
