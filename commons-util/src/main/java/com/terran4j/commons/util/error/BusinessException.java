package com.terran4j.commons.util.error;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StringUtils;

import com.terran4j.commons.util.value.ResourceBundlesProperties;
import com.terran4j.commons.util.value.RichProperties;

/**
 * 业务异常基类。
 * 
 * @author jiangwei
 */
public class BusinessException extends Exception {

	private static final long serialVersionUID = 5988465338967853686L;

	/**
	 * 异常信息对应的文案资源。
	 */
	private static final Map<Class<? extends ErrorCode>, ResourceBundle> bundles = new ConcurrentHashMap<>();

	/**
	 * 表示不存在的ResourceBundle
	 */
	private static final ResourceBundle NONEXISTENT_BUNDLE = new ResourceBundle() {
        public Enumeration<String> getKeys() { return null; }
        protected Object handleGetObject(String key) { return null; }
        public String toString() { return "NONEXISTENT_BUNDLE"; }
    };

	private final Stack<RichProperties> info = new Stack<RichProperties>();
	
	private final ErrorCode code;
	
	public BusinessException(String code) {
		this(code, Locale.getDefault(), new Throwable(code));
	}
	
	public BusinessException(String code, Throwable e) {
		this(code, Locale.getDefault(), e);
	}
	
	public BusinessException(String code, Locale locale, Throwable e) {
		super(e);
		this.code = new ResourceErrorCode(code, locale);
		info.push(new RichProperties());
		String message = getMessage(code, locale);
		if (StringUtils.hasText(message)) {
			info.peek().setMessage(message);
		}
	}
	
	public static final String getMessage(String code) {
		return getMessage(code, Locale.getDefault());
	}
	
	public static final String getMessage(String code, Locale locale) {
		ResourceBundlesProperties props = null;
		try {
			props = ResourceBundlesProperties.get("error", locale);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		if (props != null && props.get(code) != null) {
			return props.get(code);
		}
		return null;
	}

	@Deprecated
	public BusinessException(ErrorCode code) {
		super();
		this.code = code;
		info.push(new RichProperties());
		info.peek().setMessage(getResource(code.getName()));
	}
	
	public BusinessException(ErrorCode code, Throwable cause) {
		super(cause);
		this.code = code;
		info.push(new RichProperties());
		info.peek().setMessage(getResource(code.getName()));
	}
	
	public final BusinessException reThrow(String message) {
		RichProperties newInfo = new RichProperties().setMessage(message);
		info.push(newInfo);
		return this;
	}
	
	/**
	 * 获取异常的描述信息。
	 */
	@Override
	public String getMessage() {
		return getInfo().getMessage();
	}
	
	public final BusinessException setMessage(String message) {
		getInfo().setMessage(message);
		return this;
	}

	public final BusinessException put(String key, Object value) {
		getInfo().put(key, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends BusinessException> T as(Class<T> clazz) {
		if (clazz != getClass()) {
			String msg = String.format("clazz must be: {}", getClass().getName());
			throw new IllegalArgumentException(msg);
		}
		return (T) this;
	}

	public final Object get(String key) {
		return getInfo().get(key);
	}

	public ErrorCode getErrorCode() {
		return code;
	}
	
	RichProperties getInfo() {
		return info.peek();
	}
	
	Stack<RichProperties> getInfoStack() {
		return info;
	}
	
	/**
	 * 
	 * @return
	 */
	private final ResourceBundle getBundle() {
		ResourceBundle bundle = bundles.get(code.getClass());
		if (bundle != null) {
			return bundle == NONEXISTENT_BUNDLE ? null : bundle;
		}
		
		String path = code.getClass().getName().replace('.', '/');
		try {
		    bundle = ResourceBundle.getBundle(path);
		} catch (MissingResourceException e) {
		    // ignore.
        }
		if (bundle == null) {
		    path = code.getClass().getSimpleName();
		    try {
	            bundle = ResourceBundle.getBundle(path);
	        } catch (MissingResourceException e) {
	            // ignore.
	        }
		}
		
		// 缓存 bundle 对象。
		bundles.put(code.getClass(), bundle == null ? NONEXISTENT_BUNDLE : bundle);

		return bundle;
	}

	private String getResource(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		key = key.trim();

		String message = null;
		ResourceBundle bundle = getBundle();
		if (bundle != null) {
		    try {
		        message = bundle.getString(key);
		    } catch (MissingResourceException e) {
		        // ignore.
            }
		}

		if (StringUtils.isEmpty(message)) {
			message = key.replace('.', ' ');
		}
		return message;
	}

	/**
	 * 获取异常的详细信息。
	 * 
	 * @return 异常详细信息。
	 */
	public ErrorReport getReport() {
		ErrorReport topReport = null;
		Throwable currentThrowable = this;
		ErrorReport currentReport = null;
		ErrorReport previousReport = null;
		while (currentThrowable != null) {
			currentReport = new ErrorReport(currentThrowable);
			if (topReport == null) {
				topReport = currentReport;
			}
			if (previousReport != null) {
				previousReport.setCause(currentReport);
			}
			previousReport = currentReport;
			currentThrowable = currentThrowable.getCause();
		}
		return topReport;
	}
	
	public Map<String, Object> getProps() {
		return getInfo().getAll();
	}

}
