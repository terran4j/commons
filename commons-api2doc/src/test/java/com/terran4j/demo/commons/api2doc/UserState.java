package com.terran4j.demo.commons.api2doc;

import com.terran4j.commons.api2doc.annotations.ApiComment;

public enum UserState {
	
	@ApiComment("启用")
	open,

	@ApiComment("禁用")
	close,

}