package com.terran4j.commons.httpinvoker;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.terran4j.commons.util.error.BusinessException;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WrappedResponse {
	
    public static final int SUCCESS_CODE = 0;
    
    public static final String SUCCESS_MESSAGE = "success";
    
    public static final String KEY_resultCode = "resultCode";
    
    public static final String KEY_message = "message";
    
    public static final String KEY_data = "data";

    private String requestId = UUID.randomUUID().toString();

    private long serverTime = System.currentTimeMillis();

    private int resultCode;

    private Object data;
    
    private String message;
    
    public static WrappedResponse success() {
    		WrappedResponse response = new WrappedResponse();
        response.setResultCode(SUCCESS_CODE);
        response.setMessage(SUCCESS_MESSAGE);
        return response;
    }
    
    public static WrappedResponse success(Object data) {
        WrappedResponse response = new WrappedResponse();
        response.setResultCode(SUCCESS_CODE);
        response.setMessage(SUCCESS_MESSAGE);
        response.setData(data);
        return response;
    }
    
    public static WrappedResponse fail(BusinessException e) {
        WrappedResponse response = new WrappedResponse();
        response.setResultCode(e.getErrorCode().getValue());
        response.setMessage(e.getMessage());
        response.setData(e.getProps());
        return response;
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

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
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

}
