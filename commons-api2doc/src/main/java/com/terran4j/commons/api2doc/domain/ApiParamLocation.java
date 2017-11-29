package com.terran4j.commons.api2doc.domain;

import java.lang.reflect.Parameter;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * API 参数在 HTTP 协议中的位置。
 * 
 * @author jiangwei
 *
 */
public enum ApiParamLocation {

	Header {
		@Override
		boolean doCollect(ApiParamObject apiParamObject, Parameter param) {
			RequestHeader requestHeader = param.getAnnotation(RequestHeader.class);
			if (requestHeader == null) {
				return false;
			}
			
			String name = param.getName();
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
		boolean doCollect(ApiParamObject apiParamObject, Parameter param) {
			RequestParam requestParam = param.getAnnotation(RequestParam.class);
			if (requestParam == null) {
				return false;
			}
			
			String name = param.getName();
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
		boolean doCollect(ApiParamObject apiParamObject, Parameter param) {
			PathVariable pathVariable = param.getAnnotation(PathVariable.class);
			if (pathVariable == null) {
				return false;
			}
			
			String name = param.getName();
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
	
	public static final ApiParamLocation[] ORDER = new ApiParamLocation[] {
			Param, Path, Header
	};
	
	
	abstract boolean doCollect(ApiParamObject apiParamObject, Parameter param);
	
	public static final void collects(ApiParamObject apiParamObject, Parameter param) {
		ApiParamLocation currentLocation = null;
		for (ApiParamLocation location : ORDER) {
			if (location.doCollect(apiParamObject, param)) {
				currentLocation = location;
				break;
			}
		}
		
		if (currentLocation != null) {
			apiParamObject.setLocation(currentLocation);
			
			String sample = apiParamObject.getSample().getValue();
			if (ValueConstants.DEFAULT_NONE.equals(sample)) {
				apiParamObject.setSample("");
			}
		}
	}
}