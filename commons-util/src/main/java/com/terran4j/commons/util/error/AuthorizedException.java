package com.terran4j.commons.util.error;

import java.util.Locale;

public class AuthorizedException extends BusinessException{
    public AuthorizedException(String code) {
        super(code);
    }

    public AuthorizedException(String code, Object... args) {
        super(code, args);
    }

    public AuthorizedException(String code, Throwable e) {
        super(code, e);
    }

    public AuthorizedException(String code, Locale locale, Throwable e, Object... args) {
        super(code, locale, e, args);
    }

    public AuthorizedException(ErrorCode code, Throwable cause) {
        super(code, cause);
    }
}
