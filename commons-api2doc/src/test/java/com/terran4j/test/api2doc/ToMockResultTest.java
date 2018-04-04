package com.terran4j.test.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Date;

@Api2Doc(id = "toMockResult")
@RequestMapping(value = "/api/v1/toMockResult")
public class ToMockResultTest extends BaseApi2DocTest {

    public static class User {

        private Date createTime;

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
    }

    @RequestMapping(value = "/getDate")
    public Date getDate(Date date) {
        return date;
    }


    @Test
    public void testDateResultType() throws Exception {
        ApiDocObject doc = loadDoc("getDate");
        Object mockResult = doc.toMockResult();
        Assert.assertNotNull(mockResult);
        Assert.assertEquals(Date.class, mockResult.getClass());
    }

    @RequestMapping(value = "/getUser")
    public User getUser() {
        return new User();
    }

    @Test
    public void testDateInResultType() throws Exception {
        ApiDocObject doc = loadDoc("getUser");
        Object mockResult = doc.toMockResult();
        Assert.assertNotNull(mockResult);
        if (mockResult instanceof User) {
            User user = (User) mockResult;
            Assert.assertNotNull(user.createTime);
        } else {
            Assert.fail(mockResult.getClass() + "is NOT " + User.class);
        }
    }
}
