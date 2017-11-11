package com.terran4j.test.commons.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDataType;
import com.terran4j.commons.api2doc.domain.ApiResultObject;
import com.terran4j.commons.util.value.KeyedList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApiResultObjectTest {

    private static final Logger log = LoggerFactory.getLogger(ApiResultObjectTest.class);

    public enum UserState {

        @ApiComment("启用")
        open,

        @ApiComment("禁用")
        close,

    }

    public static class User {

        @Api2Doc(order = 10)
        @ApiComment(value = "账号id", sample = "123")
        private Long id;

        @Api2Doc(order = 20)
        @ApiComment(value = "账号用户名", sample = "terran4j")
        private String username;

        @Api2Doc(order = 30)
        @ApiComment(value = "账号密码", sample = "sdfi23skvs")
        private String password;

        @Api2Doc(order = 30)
        @ApiComment(value = "用户状态", sample = "open")
        private UserState state;

        @Api2Doc(order = 40)
        @ApiComment(value = "是否已删除", sample = "true")
        private Boolean deleted;

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public UserState getState() {
            return state;
        }

        public void setState(UserState state) {
            this.state = state;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }
    }

    public final List<String> getList() {
        return new ArrayList<String>();
    }

    public final Set<String> getSet() {
        return new HashSet<String>();
    }

    public final String[] getArray() {
        return new String[0];
    }

    public final Map<String, Object> getMap() {
        return new HashMap<String, Object>();
    }

    public final List<User> getUsers() {
        return new ArrayList<>();
    }

    @Test
    public void testParseResultType() throws Exception {
        log.info("testParseResultType");
        Method method = ReflectionUtils.findMethod(getClass(), "getUsers");
        Assert.assertNotNull(method);
        KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
        ApiResultObject object = ApiResultObject.parseResultType(method, totalResults);
        Assert.assertNotNull(object);
        Assert.assertEquals(1, totalResults.size());
        Assert.assertTrue(object == totalResults.get(0));
        Assert.assertTrue(ApiDataType.ARRAY == object.getDataType());
        Assert.assertTrue(User.class == object.getSourceType());
    }

    @Test
    public void testGetArrayElementClass() throws Exception {
        log.info("testGetArrayElementClass");
        Method method = ReflectionUtils.findMethod(getClass(), "getList");
        Assert.assertNotNull(method);
        Class<?> clazz = ApiResultObject.getArrayElementClass(method);
        Assert.assertEquals(String.class, clazz);

        method = ReflectionUtils.findMethod(getClass(), "getSet");
        Assert.assertNotNull(method);
        clazz = ApiResultObject.getArrayElementClass(method);
        Assert.assertEquals(String.class, clazz);

        method = ReflectionUtils.findMethod(getClass(), "getArray");
        Assert.assertNotNull(method);
        clazz = ApiResultObject.getArrayElementClass(method);
        Assert.assertEquals(String.class, clazz);
    }

}
