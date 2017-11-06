package com.terran4j.commons.util;

import org.springframework.util.StringUtils;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;
import com.terran4j.commons.util.error.ErrorCodes;

public class Checker {

	public static final void checkNotNull(String value, String key) throws BusinessException {
		if (StringUtils.isEmpty(value)) {
			throw new BusinessException(ErrorCodes.NULL_PARAM)
					.put(CommonErrorCode.KEY, key);
		}
	}
	
	public static final String checkLength(String value, int maxLenth, String key) throws BusinessException {
		if (StringUtils.isEmpty(value)) {
			return value;
		}
		value = value.trim();
		if (value.length() > maxLenth) {
			throw new BusinessException(ErrorCodes.INVALID_PARAM)
					.put(CommonErrorCode.KEY, key)
					.put(CommonErrorCode.MAX_LENGTH, maxLenth)
					.put(CommonErrorCode.ACUTAL_LENGTH, value.length())
					.setMessage("参数${key}值太长了");
		}
		return value;
	}
}
