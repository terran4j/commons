package com.terran4j.demo.commons.api2doc;

import java.util.ArrayList;
import java.util.List;

import com.terran4j.commons.api2doc.annotations.ApiComment;

public class UserPage {

	@ApiComment("There are many users!")
	private List<User> users = new ArrayList<>();

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
