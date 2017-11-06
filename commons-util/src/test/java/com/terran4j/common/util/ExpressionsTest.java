package com.terran4j.common.util;

import com.terran4j.commons.util.Expressions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class ExpressionsTest {

    public static class User {

        private String name;

        private Date birthday;

        public User() {
        }

        public User(String name, Date birthday) {
            this.name = name;
            this.birthday = birthday;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
    }

    @Test
    public void testParseWithResultType() throws Exception {
        Map<String, Object> args = new HashMap<>();
        User user = new User("neo", new Date());
        args.put("user", user);

        String name = Expressions.parse("#user.name", args, String.class);
        Assert.assertEquals("neo", name);
        Date birthday = Expressions.parse("#user.birthday", args, Date.class);
        Assert.assertEquals(user.getBirthday(), birthday);
    }

    @Test
    public void testParseWithoutResultType() throws Exception {
        Map<String, Object> args = new HashMap<>();
        User user = new User("neo", new Date());
        args.put("user", user);

        Object name = Expressions.parse("#user.name", args);
        Assert.assertEquals("neo", name);
        Object birthday = Expressions.parse("#user.birthday", args);
        Assert.assertEquals(user.getBirthday(), birthday);
    }
}
