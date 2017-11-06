package com.terran4j.test.commons.api2doc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.demo.commons.api2doc.User;

public class MyBean {
	
	@ApiComment("是否打开")
	private boolean open;
	
	@ApiComment("计数器")
	private int counter;
	
	private String message;
	
	private Date createTime;

	private List<User> users = new ArrayList<>();
	
	private MyBean[] children2 = new MyBean[0];
	
	private Set<MyBean> children3 = new HashSet<>();

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public MyBean[] getChildren2() {
		return children2;
	}

	public void setChildren2(MyBean[] children2) {
		this.children2 = children2;
	}

	public Set<MyBean> getChildren3() {
		return children3;
	}

	public void setChildren3(Set<MyBean> children3) {
		this.children3 = children3;
	}

}