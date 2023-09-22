package com.terran4j.commons.util.error;

import com.terran4j.commons.util.value.ResourceBundlesProperties;
import lombok.Data;

import java.io.IOException;
import java.util.Locale;

@Data
public class ErrorMessage {
    private String code;
    private String message;

    protected ErrorMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorMessage from(Locale locale, String code, Object... args){
        String message = "";
        ResourceBundlesProperties props = null;
        try {
            props = ResourceBundlesProperties.get("error", locale);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        if (props != null && props.get(code) != null) {
            message = props.get(code, args);
        }
        return new ErrorMessage(code, message);
    }

}
