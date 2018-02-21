package com.terran4j.test.api2doc;

import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.impl.Api2DocObjectFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Api2DocObjectFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(Api2DocObjectFactoryTest.class);

    public enum UserType {

        @ApiComment("ROOT用户")
        root,

        @ApiComment("管理员用户")
        admin,

        @ApiComment("普通用户")
        user,

    }

    public static class User {

        @ApiComment(value = "用户id", sample = "123")
        private Long id;

        @ApiComment(value = "用户名", sample = "neo")
        private String name;

        @ApiComment(value = "用户类型", sample = "admin")
        private UserType type;

        @ApiComment(value = "是否已删除", sample = "true")
        private Boolean deleted;

        @ApiComment(value = "头衔", sample = "3")
        private String[] titles;

        @ApiComment(value = "配偶")
        private User mate;

        @ApiComment(value = "子女", sample = "2")
        private List<User> children;

        public User getMate() {
            return mate;
        }

        public void setMate(User mate) {
            this.mate = mate;
        }

        public List<User> getChildren() {
            return children;
        }

        public void setChildren(List<User> children) {
            this.children = children;
        }

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

        public UserType getType() {
            return type;
        }

        public void setType(UserType type) {
            this.type = type;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }

        public String[] getTitles() {
            return titles;
        }

        public void setTitles(String[] titles) {
            this.titles = titles;
        }
    }

    @Test
    public void testCreateList() throws Throwable {
        List<User> users = Api2DocObjectFactory.createList(User.class, 3);
        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.size());
        Assert.assertEquals(new Long(123), users.get(0).getId());
    }

    @Test
    public void testCreateArray() throws Throwable {
        User[] users = Api2DocObjectFactory.createArray(User.class, 3);
        Assert.assertNotNull(users);
        Assert.assertEquals(3, users.length);
        Assert.assertEquals(new Long(123), users[0].getId());
    }

    @Test
    public void testCreateObject() throws Throwable {
        User user = Api2DocObjectFactory.createBean(User.class);
        Assert.assertNotNull(user);
        Assert.assertEquals(new Long(123), user.getId());
        Assert.assertEquals("neo", user.getName());
        Assert.assertEquals(true, user.getDeleted());
        Assert.assertEquals(UserType.admin, user.getType());
    }


}
