package com.terran4j.commons.api2doc.domain;

import org.springframework.util.StringUtils;

import com.terran4j.commons.api2doc.annotations.Api2Doc;

public class ApiDocUtils {

	public static final String getId(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}

		Api2Doc api2doc = clazz.getAnnotation(Api2Doc.class);

		if (api2doc != null) {
			String id = api2doc.id();
			if (StringUtils.hasText(id)) {
				return id;
			}

			String value = api2doc.value();
			if (StringUtils.hasText(value)) {
				return value;
			}
		}

		return clazz.getName();
	}
}
