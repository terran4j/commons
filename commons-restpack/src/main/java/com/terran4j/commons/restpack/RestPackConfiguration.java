package com.terran4j.commons.restpack;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * RestPack Spring配置。
 *
 * @author wei.jiang
 */
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

//        // 加了 @RestPackIgnore 注解的字段，不参与序列化。
//        objectMapper.addMixIn(Object.class, RestPackIgnore.class);


        if (log.isInfoEnabled()) {
            log.info("created ObjectMapper for RestPack.");
        }
        return objectMapper;
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
        HttpMessageConverter<?> restPackConverter = new RestPackMessageConverter(getObjectMapper());
        converters.add(0, restPackConverter);
    }

}
