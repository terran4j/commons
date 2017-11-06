package com.terran4j.commons.restpack;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terran4j.commons.util.Jsons;

/**
 * RestPack Spring配置。
 * 
 * @author wei.jiang
 *
 */
@Configuration
public class RestPackConfiguration extends WebMvcConfigurerAdapter {

	private final ObjectMapper objectMapper = Jsons.createObjectMapper();

	@Bean
	public HttpErrorHandler httpErrorHandler() {
		return new HttpErrorHandler();
	}

	@Bean
	public RestPackAspect restPackAspect() {
		return new RestPackAspect(objectMapper);
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
		HttpMessageConverter<?> restPackConverter = new RestPackMessageConverter(objectMapper);
		converters.add(0, restPackConverter);
	}

}
