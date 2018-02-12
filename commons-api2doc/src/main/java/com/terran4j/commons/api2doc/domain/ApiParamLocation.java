package com.terran4j.commons.api2doc.domain;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;

/**
 * API 参数在 HTTP 协议中的位置。
 *
 * @author jiangwei
 */
public enum ApiParamLocation {

    Header {
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

    Param {
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

    Path {
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

    public static final ApiParamLocation[] API_PARAM_LOCATIONS =
            new ApiParamLocation[]{Param, Path, Header};

    abstract boolean doCollect(ApiParamObject apiParamObject, AnnotatedElement param);

    public static final void collects(ApiParamObject apiParamObject, AnnotatedElement element) {
        ApiParamLocation currentLocation = ApiParamLocation.Param;
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