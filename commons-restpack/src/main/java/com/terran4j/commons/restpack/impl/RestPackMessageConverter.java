package com.terran4j.commons.restpack.impl;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestPackMessageConverter extends MappingJackson2HttpMessageConverter {

	public RestPackMessageConverter(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		if (RestPackAspect.isRestPack()) {
			return true;
		}
		return super.canWrite(clazz, mediaType);
	}

}
