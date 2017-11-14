package com.terran4j.test.commons.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class Api2DocCollectorTest {

    private static final Logger log = LoggerFactory.getLogger(Api2DocCollectorTest.class);

    public static class User {

        @ApiComment(value = "账号id", sample = "123")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Api2Doc(id = "user", name = "用户相关接口", order = 0)
    @RestController
    @RequestMapping(value = "/user")
    public class UserController {

        @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
        public User getUser(@PathVariable("id") Long id) {
            return null;
        }

        @RequestMapping(value = "/users", method = RequestMethod.GET)
        public List<User> getUserList() {
            return null;
        }

        @RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
        public User insert(@PathVariable("id") Long id, //
                           @ApiComment(value = "用户名", sample = "张三")
                           @RequestParam("name") String name) {
            return null;
        }

        @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
        public String delete(@PathVariable("id") Long id) {
            return "OK";
        }
    }

    @Test
    public void testToApiFolder() throws Exception {
        log.info("testToApiFolder");
        Api2DocCollector collector = new Api2DocCollector();
        ApiFolderObject folder = collector.toApiFolder(new UserController(), "userController");
        Assert.assertNotNull(folder);
        Assert.assertEquals(4, folder.getDocs().size());

        // 按 order 及 方法中排序。
        Assert.assertEquals("delete", folder.getDocs().get(0).getId());
        Assert.assertEquals("getUser", folder.getDocs().get(1).getId());
        Assert.assertEquals("getUserList", folder.getDocs().get(2).getId());
        Assert.assertEquals("insert", folder.getDocs().get(3).getId());
    }

}
