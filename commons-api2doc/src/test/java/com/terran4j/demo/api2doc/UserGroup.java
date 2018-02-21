package com.terran4j.demo.api2doc;

import java.util.ArrayList;
import java.util.List;

import com.terran4j.commons.api2doc.annotations.ApiComment;

@ApiComment("一组用户的信息")
public class UserGroup {

    @ApiComment("本组用户的组")
    private String group;

	@ApiComment(value = "本组用户列表", sample = "2")
	private List<User> users = new ArrayList<>();

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
