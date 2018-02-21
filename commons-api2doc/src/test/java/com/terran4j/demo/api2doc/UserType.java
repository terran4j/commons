package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.ApiComment;

public enum UserType {
	
	@ApiComment("管理员")
	admin,

	@ApiComment("普通用户")
    user,

}