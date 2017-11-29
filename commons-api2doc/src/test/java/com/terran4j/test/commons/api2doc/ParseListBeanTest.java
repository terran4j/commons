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

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiResultObject;
import com.terran4j.commons.util.value.KeyedList;

@RunWith(SpringJUnit4ClassRunner.class)
public class ParseListBeanTest {

	private static final Logger log = LoggerFactory.getLogger(ParseListBeanTest.class);

	public class User {

		@Api2Doc(order = 10)
		@ApiComment(value = "账号id", sample = "123")
		private Long id;
		
		@Api2Doc(order = 20)
		@ApiComment(value = "账号用户名", sample = "terran4j")
		private String username;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
		
	}
	
	public static class ListBean {

		@ApiComment("There are many users!")
		private List<User> users = new ArrayList<>();

		public List<User> getUsers() {
			return users;
		}

		public void setUsers(List<User> users) {
			this.users = users;
		}

	}
	
	public final ListBean getListBean() {
		return new ListBean();
	}
	
	@Test
	public void testGetSourceType() throws Exception {
		log.info("testGetSourceType");
		Method method = ReflectionUtils.findMethod(getClass(), "getListBean");
		ApiResultObject results = ApiResultObject.parseResultType(method,  new KeyedList<>());
		Assert.assertNotNull(results);
		log.info("results: {}", results);
		Assert.assertTrue(results.getChildren().size() == 1);
		ApiResultObject user = results.getChildren().get(0);
		Assert.assertEquals(User.class, user.getSourceType());
	}

	@Test
	public void testParseListBean() throws Exception {
		log.info("testParseListBean");
		Method method = ReflectionUtils.findMethod(getClass(), "getListBean");
		Assert.assertNotNull(method);

		KeyedList<String, ApiResultObject> list = new KeyedList<>();
		ApiResultObject results = ApiResultObject.parseResultType(method, list);
		Assert.assertNotNull(results);
		log.info("results: {}", results);
		Assert.assertTrue(list.size() == 2);

		ApiResultObject resultTop = list.get(0);
		log.info("resultTop: {}", resultTop);
		Assert.assertTrue(resultTop.getChildren().size() == 1);
		ApiResultObject users = resultTop.getChildren().get(0);
		Assert.assertEquals("users", users.getId());
		Assert.assertEquals("There are many users!", users.getComment().getValue());

		ApiResultObject resultUser = list.get(1);
		log.info("resultUser: {}", resultUser);
		Assert.assertEquals("users", resultUser.getId());
		Assert.assertTrue(resultUser.getChildren().size() == 2);
		ApiResultObject userId = resultUser.getChildren().get(0);
		Assert.assertEquals("id", userId.getId());
		Assert.assertEquals("账号id", userId.getComment().getValue());
	}

}
