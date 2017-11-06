package com.terran4j.commons.api2doc.codewriter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiResultObject;
import com.terran4j.commons.api2doc.impl.ClasspathFreeMarker;

import freemarker.template.Template;

@Service
public class EnumCodeWriter {
	
	private static final Logger log = LoggerFactory.getLogger(EnumCodeWriter.class);

	@Autowired
	private ClasspathFreeMarker classpathFreeMarker;

	private Template enumTemplate = null;

	@PostConstruct
	public void init() {
		try {
			enumTemplate = classpathFreeMarker.getTemplate(getClass(), //
					"enum.java.ftl");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void writeCode(Class<?> currentClass, String className, //
			CodeOutput out, CodeConfig config) throws Exception {
		
		if (currentClass == null || !currentClass.isEnum()) {
			return;
		}
		
		Map<String, Object> model = new HashMap<>();
		
		model.put("class", className);

		if (config == null) {
			config = new CodeConfig();
		}
		model.put("config", config);
		
		List<EnumInfo> enumInfos = new ArrayList<>();
		Class<Enum<?>> enumClass = (Class<Enum<?>>) currentClass;
		Enum[] enums = enumClass.getEnumConstants();
		for (Enum enumObject : enums) {
			
			EnumInfo enumInfo = new EnumInfo();
			
			String name = enumObject.name();
			enumInfo.setName(name);
			
			String comment = null;
			Field field = null;
			try {
				field = enumClass.getDeclaredField(name);
			} catch (NoSuchFieldException | SecurityException e1) {
				log.error("Can't get field \"" + name + "\" from Enum: " //
						+ enumClass.getName(), e1);
				continue;
			}
			ApiComment apiComment = field.getAnnotation(ApiComment.class);
			if (apiComment != null && StringUtils.hasText(apiComment.value())) {
				comment = ApiResultObject.getComment(apiComment);
			}
			enumInfo.setComment(comment);
			
			enumInfos.add(enumInfo);
		}
		model.put("enums", enumInfos);
		
		String code = classpathFreeMarker.build(enumTemplate, model);
		out.writeCodeFile(className + ".java", code);
	}
	
	public static final class EnumInfo {
		
		private String comment;
		
		private String name;

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}

