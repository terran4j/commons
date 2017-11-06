package com.terran4j.commons.util.error;

import org.springframework.util.StringUtils;

import com.terran4j.commons.util.Strings;

/**
 * 
 * 
 * @author jiangwei
 */
public interface ErrorCode {

	/**
	 * 
	 * @return
	 */
	int getValue();
	
	String getName();
	
	default String getMessage() {
		return null;
	}
	
	default String[] getRequiredFields() {
		return null;
	}
	
	static String[] toArray(String keys) {
		if (StringUtils.isEmpty(keys)) {
			return null;
		}
		return Strings.splitWithTrim(keys);
	}
}
