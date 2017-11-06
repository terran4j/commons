package com.terran4j.commons.api2doc.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.terran4j.commons.util.Encoding;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class ClasspathFreeMarker {

	private final Map<String, Template> templates = new HashMap<String, Template>();

	private final Configuration freeMarker;

	public ClasspathFreeMarker() {
		super();

		try {
			freeMarker = new Configuration(Configuration.VERSION_2_3_25);
			freeMarker.setDefaultEncoding(Encoding.UTF8.getName());
			TemplateLoader ctl = new ClassTemplateLoader(getClass(), "/");
			freeMarker.setTemplateLoader(ctl);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getPath(Class<?> clazz, String fileName) {
		String path = clazz.getPackage().getName().replace('.', '/') + "/" + fileName;
		return path;
	}

	public final Template getTemplate(Class<?> clazz, String fileName) {
		String path = getPath(clazz, fileName);

		Template template = templates.get(path);
		if (template != null) {
			return template;
		}

		synchronized (ClasspathFreeMarker.class) {
			template = templates.get(path);
			if (template != null) {
				return template;
			}

			try {
				template = freeMarker.getTemplate(path);
				templates.put(path, template);
				return template;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public final String build(Template template, Map<String, Object> model) //
			throws IOException, TemplateException {
		if (!templates.containsValue(template)) {
			String msg = "Can't build from the tempate which NOT get by calling this method:\n"
					+ "ClasspathFreeMarker.getTemplate(Class<?> clazz, String fileName)";
			throw new UnsupportedOperationException(msg);
		}
		String html = FreeMarkerTemplateUtils.processTemplateIntoString( //
				template, model);
		return html;
	}

}
