package com.terran4j.commons.jfinger.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;

import org.springframework.util.StringUtils;

import com.terran4j.commons.jfinger.impl.Util;
import com.terran4j.commons.util.error.ErrorCode;

/**
 * 异常基类。
 * TODO: extends BussinessException
 * @author jiangwei
 */
public class BackCommandException extends RuntimeException {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 5988465338967853686L;
	
	/**
	 * 
	 */
	private static final Object NULL = new Object();
	
	/**
	 * 
	 */
	private static final Util util = new Util();
	
	/**
	 * 
	 */
	private static ResourceBundle bundle = null;
	
	private static final class ErrorInfo {
		
		final Map<String, Object> infos = new HashMap<String, Object>();
		
		final String message;

		public ErrorInfo(String message) {
			super();
			this.message = message;
		}
		
		/**
		 * @return the message
		 */
		public final String getMessage() {
			return message;
		}
		
		public void put(String key, Object value) {
			if (value == null) {
				value = NULL;
			}
			infos.put(key, value);
		}
		
		public Object get(String key) {
			Object value = infos.get(key);
			if (value == NULL) {
				return null;
			}
			return value;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private static final ResourceBundle getBundle() {
		if (bundle != null) {
			return bundle;
		}
		String path = "command.error.properties";
		bundle = ResourceBundle.getBundle(path);
		return bundle;
	}
	
	private final Stack<ErrorInfo> infos = new Stack<BackCommandException.ErrorInfo>();
	
	private final ErrorCode code;

	public BackCommandException(ErrorCode code) {
		super("Error Code: " + code.getValue());
		this.code = code;
	}
	
	public BackCommandException(ErrorCode code, String message) {
		super(message);
		this.code = code;
	}
	
	public BackCommandException(ErrorCode code, Throwable cause) {
		super(cause);
		this.code = code;
	}
	
	public BackCommandException(ErrorCode code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
	public final BackCommandException push(String msg) {
		this.infos.push(new ErrorInfo(msg));
		return this;
	}
	
	public final BackCommandException setInfo(String key, Object value) {
		if (infos.size() > 0) {
			infos.peek().put(key, value);
		} else {
			infos.push(new ErrorInfo(super.getMessage())).put(key, value);
		}
		return this;
	}
	
	public final Object getInfo(String key) {
		if (infos.size() == 0) {
			return null;
		}
		return infos.peek().get(key);
	}
	
	public static final BackCommandException wrap(ErrorCode code) {
		return new BackCommandException(code);
	}
	
	public static final BackCommandException wrap(ErrorCode code, String msg) {
		return new BackCommandException(code, msg);
	}
	
	public static final BackCommandException wrap(ErrorCode code, Throwable t) {
		if (t instanceof BackCommandException) {
			BackCommandException be = (BackCommandException) t;
			if (be.getErrorCode() == code) {
				return be.push(t.getMessage());
			}
		}
		return new BackCommandException(code, t);
	}
	
	public static final BackCommandException wrap(ErrorCode code, Throwable t, String msg) {
		if (t instanceof BackCommandException) {
			BackCommandException be = (BackCommandException) t;
			if (be.getErrorCode() == code) {
				return be.push(msg);
			}
		}
		return new BackCommandException(code, msg, t);
	}
	
	public ErrorCode getErrorCode() {
		return code;
	}
	
	private String getResource(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		key = key.trim();
		
		String message = null;
		try {
			message = getBundle().getString(key);
		} catch (MissingResourceException e) {
			// ignore it.
		}
		if (StringUtils.isEmpty(message)) {
			message = key.replace('.', ' ');
		}
		return message;
	}
	
	/**
	 * 
	 * @param element 
	 * @return 
	 */
	private String toString(StackTraceElement element) {
		StringBuilder sb = new StringBuilder();
		sb.append(element.getClassName()).append(".");
		sb.append(element.getMethodName());
		sb.append("(").append(element.getFileName());
		sb.append(":").append(element.getLineNumber()).append(")");
		return sb.toString();
	}
	
	@Override
	public String getMessage() {
		return getReport();
	}
	
	/**
	 * 获取异常的简要信息。
	 * @return 异常简要信息。
	 */
	public String getReport() {
		StringBuilder sb = new StringBuilder();
		
		Throwable t = this;
		while (t != null) {
			sb.append("\n---------------- Exception Stack ----------------\n");
			StackTraceElement[] stackTrace = t.getStackTrace();
			if (t instanceof BackCommandException) {
				BackCommandException backCommandException = (BackCommandException) t;
				sb.append(toString(stackTrace[1])).append("\n");
				backCommandException.putDetails(sb);
			} else {
				sb.append(toString(stackTrace[0])).append("\n");
				sb.append(util.getString(t));
			}
			t = t.getCause();
		}
		sb.append("\n");
		
		return sb.toString();
	}
	
	/**
	 * 获取异常的描述信息。
	 * @return
	 */
	public String getDescription() {
		return getResource(code.getName());
	}
	
	/**
	 * 
	 * @param sb
	 */
	private void putDetails(StringBuilder sb) {
		sb.append(getClass().getName());
		sb.append(": Code = ").append(code.getValue()).append(", ");
		sb.append(getDescription()).append("\n");
		while (!infos.empty()) {
			sb.append("    ------------ Error Code Stack ------------\n");
			ErrorInfo error = infos.pop();
			sb.append("    Cause by: ").append(error.getMessage()).append("\n");
			Set<String> keys = error.infos.keySet();
			if (keys != null && keys.size() > 0) {
				sb.append("    More Detail Information:\n");
				for (String key : keys) {
					Object value = error.infos.get(key);
					sb.append("    ").append(key).append(": ");
					if (value != null && value != NULL) {
						String valueText = getString(value);
						sb.append(valueText);
					}
					sb.append("\n");
				}
			}
		}
	}
	
	/**
	 * 获取对象的描述信息。
	 * @param value 具体对象。
	 * @return 对象的字符串信息。
	 */
	private String getString(Object value) {
		if (value == null) {
			return "";
		}
		
		if (value.getClass().isArray()) {
			StringBuilder sb = new StringBuilder("[");
			boolean first = true;
			Object[] array = (Object[])value;
			for (Object item : array) {
				if (!first) {
					sb.append(", ");
				}
				first = false;
				sb.append(getString(item));
			}
			sb.append("]");
			return sb.toString();
		}
		
		return value.toString();
	}
	
}
