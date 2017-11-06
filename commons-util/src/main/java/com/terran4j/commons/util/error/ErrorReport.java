package com.terran4j.commons.util.error;

import java.util.Iterator;
import java.util.Stack;

import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.value.RichProperties;

public class ErrorReport {
	
	private static final String COLON = ": ";

	private static final String INDENT = "    ";
	
	private static final String N = "\n";

	private final Throwable throwable;

	private final String className;
	
	private final String methodName;
	
	private final String fileName;
	
	private final int lineNumber;
	
	private final ErrorCode code;
	
	private final Stack<RichProperties> info;
	
	private ErrorReport cause;
	
	public ErrorReport(Throwable throwable) {
		super();
		this.throwable = throwable;
		
		StackTraceElement element = throwable.getStackTrace()[0];
		this.className = element.getClassName();
		this.methodName = element.getMethodName();
		this.fileName = element.getFileName();
		this.lineNumber = element.getLineNumber();
		
		if (throwable instanceof BusinessException) {
			BusinessException business = (BusinessException) throwable;
			this.code = business.getErrorCode();
			this.info = business.getInfoStack();
		} else {
			this.code = null;
			this.info = null;
		}
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public ErrorReport getCause() {
		return cause;
	}

	public void setCause(ErrorReport cause) {
		this.cause = cause;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public ErrorCode getCode() {
		return code;
	}

	public RichProperties getInfo() {
		if (info == null) {
			return null;
		}
		return info.peek();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb, this);
		return sb.toString();
	}
	
	private void toString(StringBuilder sb, RichProperties info, boolean hasMessage) {
		if (hasMessage) {
			sb.append(info.getMessage()).append(N);
		}
		Iterator<String> keys = info.iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = info.get(key);
			sb.append(INDENT).append(key).append(COLON);
			String valueText = Strings.toString(value);
			sb.append(valueText).append(N);
		}
	}
	
	private void toString(StringBuilder sb, ErrorReport report) {
		
		// 异常在代码中抛出的位置。
		sb.append(report.className).append(".").append(report.methodName).append("(")
				.append(report.fileName).append(COLON).append(report.lineNumber).append(")").append(N);
		
		// 异常描述。
		sb.append("Error Description").append(COLON).append(report.getThrowable().getMessage()).append(N);
		
		// 异常码
		ErrorCode code = report.code;
		if (code != null) {
			sb.append(INDENT).append("errorCode").append(COLON).append(code.getValue()).append(N)
					.append(INDENT).append("errorName").append(COLON).append(code.getName()).append(N);
		}
				
		// 异常抛出时的上下文数据信息
		RichProperties topInfo = report.getInfo();
		if (topInfo != null && topInfo.size() > 0) {
			toString(sb, topInfo, false);
		}
		Stack<RichProperties> infoStack = report.info;
		if (infoStack != null && infoStack.size() > 1) {
			for (int i = infoStack.size() - 2; i >= 0; i--) {
				RichProperties info = infoStack.get(i);
				sb.append("cause by").append(COLON);
				toString(sb, info, true);
			}
		}
		
		// 异常堆栈。
		Throwable throwable = report.throwable;
		if (!(throwable instanceof BusinessException) || report.cause == null) {
			sb.append(Strings.getString(throwable)).append(N);
		}
		
		// 异常根源（上一个异常）
		ErrorReport cause = report.cause;
		if (cause != null) {
			sb.append("cause by").append(COLON);
			toString(sb, cause);
		}
	}

}
