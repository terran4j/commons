package com.terran4j.test.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class ParseApiCommentOnParam {

	private static final Logger log = LoggerFactory.getLogger(ParseApiCommentOnParam.class);

	@ApiComment
	public class User {

		@Api2Doc(order = 10)
		@ApiComment(value = "账号id", sample = "123")
		private Long id;

		@Api2Doc(order = 20)
		@ApiComment(value = "账号用户名", sample = "neo")
		private String name;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

	public final void setUser(
	        @ApiComment(value = "用户类型", sample = "root") String type,
            boolean asRoot,
            User user) {
	}

	@Test
	public void testParseApiCommentOnParam() throws Exception {
		log.info("testParseApiCommentOnParam");
		Method method = ReflectionUtils.findMethod(getClass(), "setUser",
                new Class<?>[]{String.class, boolean.class, User.class});
		Assert.assertNotNull(method);

        Api2DocCollector collector = new Api2DocCollector();
        List<ApiParamObject> params = collector.toApiParams(method, null);
		Assert.assertEquals(4, params.size());

		ApiParamObject type = params.get(0);
		Assert.assertEquals("type", type.getId());
		Assert.assertEquals("用户类型", type.getComment().getValue());
		Assert.assertEquals("root", type.getSample().getValue());

        ApiParamObject asRoot = params.get(1);
		Assert.assertEquals("asRoot", asRoot.getId());
        Assert.assertEquals(ApiParamLocation.RequestParam, asRoot.getLocation());

        ApiParamObject id = params.get(2);
        Assert.assertEquals("id", id.getId());
        Assert.assertEquals("账号id", id.getComment().getValue());
        Assert.assertEquals("123", id.getSample().getValue());

        ApiParamObject name = params.get(3);
        Assert.assertEquals("name", name.getId());
        Assert.assertEquals("账号用户名", name.getComment().getValue());
        Assert.assertEquals("neo", name.getSample().getValue());
	}

}