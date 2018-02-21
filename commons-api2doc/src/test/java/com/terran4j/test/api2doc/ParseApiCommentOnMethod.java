package com.terran4j.test.api2doc;

import java.lang.reflect.Method;

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
public class ParseApiCommentOnMethod {

	private static final Logger log = LoggerFactory.getLogger(ParseApiCommentOnMethod.class);

	public class User {

		@Api2Doc(order = 10)
		@ApiComment(value = "账号id", sample = "123")
		private Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		@Api2Doc(order = 20)
		@ApiComment(value = "账号用户名", sample = "terran4j")
		public String getUsername() {
			return String.valueOf(id);
		}

	}

	public final User getUser() {
		return new User();
	}

	@Test
	public void testParseApiCommentOnMethod() throws Exception {
		log.info("testParseApiCommentOnMethod");
		Method method = ReflectionUtils.findMethod(getClass(), "getUser");
		Assert.assertNotNull(method);

		KeyedList<String, ApiResultObject> list = new KeyedList<>();
		ApiResultObject user = ApiResultObject.parseResultType(method, list);
		Assert.assertNotNull(user);
		log.info("user: {}", user);
		Assert.assertEquals(2, user.getChildren().size());

		ApiResultObject userId = user.getChildren().get(0);
		Assert.assertEquals("id", userId.getId());
		Assert.assertEquals("账号id", userId.getComment().getValue());
		Assert.assertEquals("123", userId.getSample().getValue());

		ApiResultObject userName = user.getChildren().get(1);
		Assert.assertEquals("username", userName.getId());
		Assert.assertEquals("账号用户名", userName.getComment().getValue());
		Assert.assertEquals("terran4j", userName.getSample().getValue());
	}

}
