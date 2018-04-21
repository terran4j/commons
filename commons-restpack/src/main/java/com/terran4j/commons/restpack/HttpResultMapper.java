package com.terran4j.commons.restpack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "terran4j.restpack.renaming")
@Service
public class HttpResultMapper {

    private String requestId = "requestId";

    private String serverTime = "serverTime";

    private String spendTime = "spendTime";

    private String resultCode = "resultCode";

    private String data = "data";

    private String message = "message";

    private String props = "props";

    private String success = "success";

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(String spendTime) {
        this.spendTime = spendTime;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Map<String, Object> toMap(HttpResult httpResult) {
        if (httpResult == null) {
            throw new NullPointerException("httpResult is null.");
        }
        Map<String, Object> map = new HashMap<>();

        String requestIdValue = httpResult.getRequestId();
        if (requestIdValue != null) {
            map.put(requestId, requestIdValue);
        }

        long serverTimeValue = httpResult.getServerTime();
        map.put(serverTime, serverTimeValue);

        long spendTimeValue = httpResult.getSpendTime();
        map.put(spendTime, spendTimeValue);

        String resultCodeValue = httpResult.getResultCode();
        if (resultCodeValue != null) {
            if (resultCodeValue.equals(HttpResult.SUCCESS_CODE)) {
                resultCodeValue = success;
            }
            map.put(resultCode, resultCodeValue);
        }

        Object dataValue = httpResult.getData();
        if (dataValue != null) {
            map.put(data, dataValue);
        }

        String messageValue = httpResult.getMessage();
        if (messageValue != null) {
            map.put(message, messageValue);
        }

        Map<String, Object> propsValue = httpResult.getProps();
        if (propsValue != null) {
            map.put(props, propsValue);
        }

        return map;
    }

    @Autowired(required = false)
    private HttpResultConverter httpResultConverter;

    public Object convert(HttpResult httpResult) {
        if (httpResultConverter != null) {
            return httpResultConverter.convert(httpResult);
        } else {
            return toMap(httpResult);
        }
    }

}
