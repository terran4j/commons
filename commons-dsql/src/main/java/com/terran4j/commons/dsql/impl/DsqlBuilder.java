package com.terran4j.commons.dsql.impl;

import com.terran4j.commons.util.Encoding;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DsqlBuilder {

	private static final Logger log = LoggerFactory.getLogger(DsqlBuilder.class);

	private static DsqlBuilder instance = null;

	public static DsqlBuilder getInstance() {
		if (instance != null) {
			return instance;
		}

		synchronized (DsqlBuilder.class) {
			if (instance != null) {
				return instance;
			}
			instance = new DsqlBuilder();
			return instance;
		}
	}

	private final Map<String, Template> templates = new HashMap<String, Template>();

	private final Configuration freeMarker;

	private DsqlBuilder() {
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
		String path = clazz.getPackage().getName().replace('.', '/')
                + "/" + fileName;
		return path;
	}

	private final Template getTemplate(Class<?> clazz, String fileName) {
		String path = getPath(clazz, fileName);

		// 检查文件是否存在。
		ClassLoader loader = clazz.getClassLoader();
		URL url = loader.getResource(path);
		if (url == null) {
			return null;
		}

		Template template = templates.get(path);
		if (template != null) {
			return template;
		}

		synchronized (DsqlBuilder.class) {
			template = templates.get(path);
			if (template != null) {
				return template;
			}

			try {
				if (log.isInfoEnabled()) {
					log.info("load freemarker template: {}", path);
				}
				template = freeMarker.getTemplate(path);
				templates.put(path, template);
				return template;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

    private final String build(Template template, Map<String, Object> model) //
			throws IOException, TemplateException {
		if (!templates.containsValue(template)) {
			String msg = "Can't build from the template which NOT get by calling this method:\n"
					+ "ClasspathFreeMarker.getTemplate(Class<?> clazz, String fileName)";
			throw new UnsupportedOperationException(msg);
		}
		String html = FreeMarkerTemplateUtils.processTemplateIntoString( //
				template, model);
		return html;
	}

	public final String buildSQL(Map<String, Object> model, Class<?> clazz, String sqlName) throws BusinessException {
        if (model == null) {
        	model = new HashMap<>();
		}

		String fileName = sqlName + ".sql.ftl";
        Template template = getTemplate(clazz, fileName);
        if (template == null) {
            throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                    .put("package", clazz.getPackage())
                    .put("fileName", fileName)
                    .setMessage("在包 ${package} 下面找不到文件： ${fileName}。");
        }

        try {
            String sql = build(template, model);
            if (log.isInfoEnabled()) {
                log.info("\nSQL（模板解析后）: \n{}\n参数: {}", sql.trim(), model);
            }
            return sql;
        } catch (IOException | TemplateException e) {
            throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                    .put("package", clazz.getPackage())
                    .put("fileName", fileName)
                    .put("params", model)
                    .setMessage("使用文件 ${fileName} 构建SQL出错：" + e.getMessage());
        }
    }


    public final String buildPreparedArgs(String sql, List<String> keys) {
        if (sql == null || sql.trim().length() == 0) {
            throw new IllegalArgumentException("sql can't be null or empty.");
        }
        sql = sql.trim();
        if (keys == null || keys.size() > 0) {
            throw new IllegalArgumentException("keys can't be null and must be an empty list.");
        }

        final String begin = "@{";
        final String end = "}";
        StringBuffer sb = new StringBuffer();
        final int size = sql.length();
        final int beginLength = begin.length();
        final int endLength = end.length();
        int from = 0;
        while (true) {
            int m = sql.indexOf(begin, from); // 变量开始位置。
            int n = sql.indexOf(end, from); // 变量结束位置。

            if (m >= 0 && m < size && n > m && n < size) {
                String s0 = sql.substring(from, m); // 变量之前的部分。
                sb.append(s0);

                // 变量定义部分。
                String matchedText = sql.substring(m, n + endLength);

                // 变量本身。
                String key = matchedText.substring(beginLength, matchedText.length() - endLength);
                keys.add(key);

                sb.append("?"); // 变量用“？”代替（SQL绑定变量的语法）。

                from = n + endLength;
            } else {
                break;
            }
        }
        if (from < size) {
            sb.append(sql.substring(from));
        }

        return sb.toString();
    }

}
