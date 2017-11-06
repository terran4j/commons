package com.terran4j.commons.util.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可以从类文件中获取多个相同路径的资源文件，合并后形成一个 Properties 。
 * 
 * @author jiangwei
 *
 */
public class ResourceBundlesProperties implements ValueSource<String, String> {

	private static final Map<String, ResourceBundlesProperties> cache = new ConcurrentHashMap<>();

	public static final ResourceBundlesProperties get(String path, Locale locale) throws IOException {
		if (path == null) {
			throw new NullPointerException();
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		
		String cacheKey = path + "_" + locale.getLanguage() + "_" + locale.getCountry();
		ResourceBundlesProperties props = cache.get(cacheKey);
		if (props != null) {
			return props;
		}

		synchronized (ResourceBundlesProperties.class) {
			props = cache.get(cacheKey);
			if (props != null) {
				return props;
			}

			Properties srcProps = load(path, locale);
			props = new ResourceBundlesProperties(srcProps);
			cache.put(cacheKey, props);

			return props;
		}
	}
	
	private static final Properties load(String path, Locale locale) throws IOException {
		String defaultPath = path + ".properties";
		Properties props = load(defaultPath);
		if (props == null) {
			props = new Properties();
		}
		
		String fixedPath = path + "_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties";
		Properties fixedProps = load(fixedPath);
		if (fixedProps != null) {
			props.putAll(fixedProps);
		}
		
		return props;
	}

	private static final Properties load(String path) throws IOException {

		Enumeration<URL> urls = ResourceBundlesProperties.class.getClassLoader().getResources(path);
		if (urls == null || !urls.hasMoreElements()) {
			return null;
		}

		Properties props = new Properties();
		
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			InputStream in = url.openStream();
			if (in != null) {
				try {
					props.load(new InputStreamReader(in, "UTF-8"));
				} finally {
					try {
						in.close();
					} catch (Exception e1) {
						// ignore.
					}
				}
			}
		}

		return props;
	}

	private final Properties props;

	public ResourceBundlesProperties(Properties props) {
		super();
		this.props = props;
	}

	@Override
	public String get(String key) {
		if (key == null || props == null) {
			return null;
		}
		return props.getProperty(key);
	}

}
