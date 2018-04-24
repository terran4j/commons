package com.terran4j.commons.restpack.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.terran4j.commons.restpack.impl.*;
import com.terran4j.commons.restpack.log.RestPackLogAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RestPack Spring配置。
 *
 * @author wei.jiang
 */
@PropertySource("classpath:restpack/freemarker.properties")
@EnableConfigurationProperties(HttpResultMapper.class)
@Configuration
public class RestPackConfiguration extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(RestPackConfiguration.class);

    private static ObjectMapper objectMapper = null;

    public static final ObjectMapper getObjectMapper() {
        if (objectMapper != null) {
            return objectMapper;
        }

        synchronized (RestPackConfiguration.class) {
            if (objectMapper != null) {
                return objectMapper;
            }

            objectMapper = createObjectMapper();
            return objectMapper;
        }
    }

    /**
     * RestPack 专有的 json 序列化器，与通用的有些不一样。
     *
     * @return json 序列化器
     */
    private static final ObjectMapper createObjectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

        // 属性为空时（包括 null, 空串，空集合，空对象），不参与序列化。
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Date 对象在序列化时，格式为 yyyy-MM-dd HH:mm:ss 。
        // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 注意： 客户端通常喜欢接收 long 类型的时间格式，因此这里不要将日期格式化了。

        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        // json串以良好的格式输出。
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // 当属性为空或有问题时不参与序列化。
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 未知的属性不参与反序列化。
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (log.isInfoEnabled()) {
            log.info("created ObjectMapper for RestPack.");
        }
        return objectMapper;
    }


    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 注册一个自动将请求参数转为 Date 类型的转化器。
     */
//    @PostConstruct // TODO： 目前这个会影响其它功能的，暂不启用。
    public void addConversionConfig() {
        RequestMappingHandlerAdapter handlerAdapter = applicationContext
                .getBean(RequestMappingHandlerAdapter.class);
        if (handlerAdapter == null) {
            if (log.isInfoEnabled()) {
                log.info("handlerAdapter is null.");
            }
            return;
        }

        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer)
                handlerAdapter.getWebBindingInitializer();
        if (initializer == null) {
            if (log.isInfoEnabled()) {
                log.info("initializer is null.");
            }
            return;
        }

        GenericConversionService genericConversionService =
                (GenericConversionService) initializer.getConversionService();
        if (genericConversionService == null) {
            if (log.isInfoEnabled()) {
                log.info("genericConversionService is null.");
            }
            return;
        }

        genericConversionService.addConverter(String.class, Date.class,
                new DateConverter());
    }

    @Bean
    public HttpErrorHandler httpErrorHandler() {
        return new HttpErrorHandler();
    }

    @Bean
    public RestPackAspect restPackAspect() {
        return new RestPackAspect();
    }

    @Bean
    public RestPackAdvice restPackAdvice() {
        return new RestPackAdvice();
    }

    @Bean
    public RestPackLogAspect restPackLogAspect() {
        return new RestPackLogAspect();
    }

    @Bean
    public RestPackConfig restPackConfig() {
        return new RestPackConfig();
    }

    @Bean
    public HttpResultMapper httpResultMapper() {
        return new HttpResultMapper();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        // 去掉 MappingJackson2HttpMessageConverter 转换器。
        List<HttpMessageConverter<?>> removedConverters = new ArrayList<HttpMessageConverter<?>>();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                removedConverters.add(converter);
            }
        }
        converters.removeAll(removedConverters);

        // 添加 RestPackMessageConverter 转换器，并放在最高优先级。
        HttpMessageConverter<?> restPackConverter =
                new RestPackMessageConverter(getObjectMapper());
        converters.add(0, restPackConverter);
    }

}