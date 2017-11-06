package com.terran4j.commons.reflux;

import com.terran4j.commons.util.error.BusinessException;

public interface RefluxClient {

	boolean connect(String serverURL, String clientId) throws BusinessException;
	
}
