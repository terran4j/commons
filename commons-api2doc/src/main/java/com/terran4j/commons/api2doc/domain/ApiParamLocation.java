package com.terran4j.commons.api2doc.domain;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.AnnotatedElement;

/**
 * API 参数在 HTTP 协议中的位置。
 *
 * @author jiangwei
 */
public enum ApiParamLocation {

    RequestHeader {
        @Override
        boolean doCollect(ApiParamObject apiParamObject, AnnotatedElement element) {
            RequestHeader requestHeader = element.getAnnotation(RequestHeader.class);
            if (requestHeader == null) {
                return false;
            }

            String name = null;
            if (StringUtils.hasText(requestHeader.value())) {
                name = requestHeader.value();
            }
            if (StringUtils.hasText(requestHeader.name())) {
                name = requestHeader.name();
            }
            apiParamObject.setName(name);

            boolean required = requestHeader.required();
            apiParamObject.setRequired(required);

            String paramSample = requestHeader.defaultValue();
            if (StringUtils.hasText(paramSample)) {
                if (ValueConstants.DEFAULT_NONE.equals(paramSample)) {
                    paramSample = "";
                }
                apiParamObject.setSample(paramSample);
            }

            return true;
        }
    },

    RequestParam {
        @Override
        boolean doCollect(ApiParamObject apiParamObject, AnnotatedElement element) {
            RequestParam requestParam = element.getAnnotation(RequestParam.class);
            if (requestParam == null) {
                return false;
            }

            String name = null;
            if (StringUtils.hasText(requestParam.value())) {
                name = requestParam.value();
            }
            if (StringUtils.hasText(requestParam.name())) {
                name = requestParam.name();
            }
            apiParamObject.setName(name);

            boolean required = requestParam.required();
            apiParamObject.setRequired(required);

            String paramSample = requestParam.defaultValue();
            if (StringUtils.hasText(paramSample)) {
                if (ValueConstants.DEFAULT_NONE.equals(paramSample)) {
                    paramSample = "";
                }
                apiParamObject.setSample(paramSample);
            }

            return true;
        }
    },

    RequestPart {
        @Override
        boolean doCollect(ApiParamObject apiParamObject, AnnotatedElement element) {
            RequestPart requestPart = element.getAnnotation(RequestPart.class);
            if (requestPart == null) {
                return false;
            }

            String name = null;
            if (StringUtils.hasText(requestPart.value())) {
                name = requestPart.value();
            }
            if (StringUtils.hasText(requestPart.name())) {
                name = requestPart.name();
            }
            apiParamObject.setName(name);

            boolean required = requestPart.required();
            apiParamObject.setRequired(required);

            return true;
        }
    },

    CookieValue {
        @Override
        boolean doCollect(ApiParamObject apiParamObject, AnnotatedElement element) {
            CookieValue cookieValue = element.getAnnotation(CookieValue.class);
            if (cookieValue == null) {
                return false;
            }

            String name = null;
            if (StringUtils.hasText(cookieValue.value())) {
                name = cookieValue.value();
            }
            if (StringUtils.hasText(cookieValue.name())) {
                name = cookieValue.name();
            }
            apiParamObject.setName(name);

            boolean required = cookieValue.required();
            apiParamObject.setRequired(required);

            String paramSample = cookieValue.defaultValue();
            if (StringUtils.hasText(paramSample)) {
                if (ValueConstants.DEFAULT_NONE.equals(paramSample)) {
                    paramSample = "";
                }
                apiParamObject.setSample(paramSample);
            }

            return true;
        }
    },

    PathVariable {
        @Override
        boolean doCollect(ApiParamObject apiParamObject, AnnotatedElement element) {
            PathVariable pathVariable = element.getAnnotation(PathVariable.class);
            if (pathVariable == null) {
                return false;
            }

            String name = null;
            if (StringUtils.hasText(pathVariable.value())) {
                name = pathVariable.value();
            }
            if (StringUtils.hasText(pathVariable.name())) {
                name = pathVariable.name();
            }
            apiParamObject.setName(name);

            boolean required = pathVariable.required();
            apiParamObject.setRequired(required);

            return true;
        }
    };

    public static final ApiParamLocation[] API_PARAM_LOCATIONS = new ApiParamLocation[]{
            RequestParam, PathVariable, RequestHeader, CookieValue, RequestPart
    };

    abstract boolean doCollect(ApiParamObject apiParamObject, AnnotatedElement param);

    public static final void collects(ApiParamObject apiParamObject, AnnotatedElement element) {
        ApiParamLocation currentLocation = ApiParamLocation.RequestParam;
        for (ApiParamLocation location : API_PARAM_LOCATIONS) {
            if (location.doCollect(apiParamObject, element)) {
                currentLocation = location;
                break;
            }
        }
        apiParamObject.setLocation(currentLocation);

        String sample = apiParamObject.getSample().getValue();
        if (ValueConstants.DEFAULT_NONE.equals(sample)) {
            apiParamObject.setSample("");
        }
    }

}