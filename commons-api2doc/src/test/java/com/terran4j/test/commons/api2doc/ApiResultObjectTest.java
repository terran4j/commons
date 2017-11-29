package com.terran4j.test.commons.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDataType;
import com.terran4j.commons.api2doc.domain.ApiResultObject;
import com.terran4j.commons.api2doc.impl.Api2DocUtils;
import com.terran4j.commons.restpack.RestPackIgnore;
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
        @RestPackIgnore
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
    public void testParseResultTypeWithIgnore() throws Exception {
        log.info("testParseResultTypeWithIgnore");
        Method method = ReflectionUtils.findMethod(getClass(), "getUsers");
        Assert.assertNotNull(method);
        KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
        ApiResultObject object = ApiResultObject.parseResultType(method, totalResults);
        Assert.assertNotNull(object);
        Assert.assertNull(object.getChild("deleted"));
    }

    public static class DateBean {

        @Api2Doc
        @ApiComment(value = "当前日期")
        private Date current;

        public Date getCurrent() {
            return current;
        }

        public void setCurrent(Date current) {
            this.current = current;
        }
    }

    public DateBean getDateBean() {
        return new DateBean();
    }

    @Test
    public void testParseResultTypeWithDate() throws Exception {
        log.info("testParseResultTypeWithDate");
        Method method = ReflectionUtils.findMethod(getClass(), "getDateBean");
        Assert.assertNotNull(method);
        KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
        ApiResultObject object = ApiResultObject.parseResultType(method, totalResults);
        Assert.assertNotNull(object);
        ApiResultObject current = object.getChild("current");
        Assert.assertEquals("long", current.getTypeName());
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

    @Test
    public void testGetArrayElementClass() throws Exception {
        log.info("testGetArrayElementClass");
        Method method = ReflectionUtils.findMethod(getClass(), "getList");
        Assert.assertNotNull(method);
        Class<?> clazz = Api2DocUtils.getArrayElementClass(method);
        Assert.assertEquals(String.class, clazz);

        method = ReflectionUtils.findMethod(getClass(), "getSet");
        Assert.assertNotNull(method);
        clazz = Api2DocUtils.getArrayElementClass(method);
        Assert.assertEquals(String.class, clazz);

        method = ReflectionUtils.findMethod(getClass(), "getArray");
        Assert.assertNotNull(method);
        clazz = Api2DocUtils.getArrayElementClass(method);
        Assert.assertEquals(String.class, clazz);
    }

    public static class NotOrderBean {

        private String abc;

        private String abd;

        private String b1;

        private String b2;

        public String getAbc() {
            return abc;
        }

        public void setAbc(String abc) {
            this.abc = abc;
        }

        public String getAbd() {
            return abd;
        }

        public void setAbd(String abd) {
            this.abd = abd;
        }

        public String getB1() {
            return b1;
        }

        public void setB1(String b1) {
            this.b1 = b1;
        }

        public String getB2() {
            return b2;
        }

        public void setB2(String b2) {
            this.b2 = b2;
        }
    }

    public NotOrderBean getNotOrderBean() {
        return null;
    }

    @Test
    public void testParseResultTypeWithNotOrder() throws Exception {
        log.info("testParseResultTypeWithNotOrder");
        Method method = ReflectionUtils.findMethod(getClass(), "getNotOrderBean");
        Assert.assertNotNull(method);
        ApiResultObject object = ApiResultObject.parseResultType(method, null);
        Assert.assertNotNull(object);
        Assert.assertEquals(4, object.getChildren().size());
        Assert.assertEquals("abc", object.getChildren().get(0).getId());
        Assert.assertEquals("abd", object.getChildren().get(1).getId());
        Assert.assertEquals("b1", object.getChildren().get(2).getId());
        Assert.assertEquals("b2", object.getChildren().get(3).getId());
    }
}
