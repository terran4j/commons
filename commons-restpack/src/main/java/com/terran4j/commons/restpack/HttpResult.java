package com.terran4j.commons.restpack;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResult {
	
	public static final String KEY_resultCode = "resultCode";
	
	public static final String KEY_data = "data";
	
	public static final String KEY_message = "message";
	
	public static final String SUCCESS_CODE = "success";

	private String requestId;

	/**
	 * 服务端开始处理的时间。
	 */
	private long serverTime;
	
	private long spendTime;

	private String resultCode;

	private Object data;
	
	private String message;
	
	private Map<String, Object> props = null;
	
	public static HttpResult success() {
		HttpResult response = new HttpResult();
		response.setResultCode(SUCCESS_CODE);
		return response;
	}

	public static HttpResult success(Object data) {
		HttpResult response = new HttpResult();
		response.setResultCode(SUCCESS_CODE);
		response.setData(data);
		return response;
	}

	private static final long defaultServerTime = System.currentTimeMillis();

	private static final String defaultRequestId = RestPackAspect.generateRequestId();

    public static HttpResult successFully(Object data) {
        HttpResult response = new HttpResult();
        response.setRequestId(defaultRequestId);
        response.setSpendTime(5);
        response.setServerTime(defaultServerTime);
        response.setResultCode(SUCCESS_CODE);
        response.setData(data);
        return response;
    }
	
	public static HttpResult fail(ErrorCode errorCode, String msg) {
		if (errorCode == null) {
			throw new NullPointerException("errorCode is null.");
		}
		if (StringUtils.isEmpty(msg)) {
			msg = errorCode.getName().replace('.', ' ');
		}
		HttpResult response = new HttpResult();
		response.setResultCode(errorCode.getName());
		response.setMessage(msg);
		return response;
	}

	public static HttpResult fail(BusinessException e) {
		HttpResult response = new HttpResult();
		response.setResultCode(e.getErrorCode().getName());
		response.setMessage(e.getMessage());
		
		Map<String, Object> errProps = e.getProps();
		if (errProps != null && errProps.size() > 0) {
			if (response.props == null) {
				response.props = new HashMap<>();
			}
		    response.props.putAll(errProps);
		}
		
		return response;
	}
	
	public void clearPropsIfEmpty() {
        if (props != null && props.size() == 0) {
            props = null;
        }
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public long getSpendTime() {
		return spendTime;
	}

	public void setSpendTime(long spendTime) {
		this.spendTime = spendTime;
	}
	
	public Map<String, Object> getProps() {
		return props;
	}

	public void setProps(Map<String, Object> props) {
		this.props = props;
	}

	public final String toString() {
	    return Strings.toString(this);
	}
	
}
