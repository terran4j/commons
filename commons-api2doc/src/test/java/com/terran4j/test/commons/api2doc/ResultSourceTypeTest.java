package com.terran4j.test.commons.api2doc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import com.terran4j.demo.commons.api2doc.User;

@RunWith(SpringJUnit4ClassRunner.class)
public class ResultSourceTypeTest {
	
	private static final Logger log = LoggerFactory.getLogger(ResultSourceTypeTest.class);
	
	public class UserList {

		private List<User> users = new ArrayList<>();

		public List<User> getUsers() {
			return users;
		}

		public void setUsers(List<User> users) {
			this.users = users;
		}
		
	}
	
	public UserList getUserList() {
		return new UserList();
	}

	@Test
	public void testGetResultSourceType() throws Exception {
		log.info("testGetResultSourceType");
		Method method = ReflectionUtils.findMethod(getClass(), "getUserList");
		Assert.assertNotNull(method);
	}
	
}
