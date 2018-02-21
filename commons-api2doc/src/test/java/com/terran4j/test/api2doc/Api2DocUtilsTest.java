package com.terran4j.test.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import com.terran4j.commons.api2doc.impl.Api2DocUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class Api2DocUtilsTest {

    private static final Logger log = LoggerFactory.getLogger(Api2DocUtilsTest.class);

    @Api2Doc(id = "user", name = "用户相关接口", order = 0)
    @RestController
    @RequestMapping(value = "/api/v1")
    public class Api2DocUtilsTestController {

        @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
        public Api2DocCollectorTest.User getUser(
                @ApiComment(value = "用户id", sample = "123")
                @PathVariable("id") Long id) {
            return null;
        }

        @RequestMapping(value = "/user2/{id}", method = RequestMethod.GET)
        public Api2DocCollectorTest.User getUser2(
                @ApiComment(value = "用户id") // 参数没有指定示例值。
                @PathVariable("id") Long id,
                @ApiComment(value = "年龄") // 参数没有指定示例值。
                @RequestParam("age") Integer age) {
            return null;
        }

        @RequestMapping(value = "/group/{groupId}/users", method = RequestMethod.GET)
        public List<Api2DocCollectorTest.User> getUsers(
                @ApiComment(value = "组id", sample = "3")
                @PathVariable("groupId") Long groupId,
                @ApiComment(value = "所在城市，如： 北京/上海/深圳", sample = "北京")
                @RequestParam("city") String city,
                @ApiComment(value = "年龄", sample = "30")
                @RequestParam("age") Integer age) {
            return null;
        }
    }

    private String serverURL = "http://localhost:8080";

    @Test
    public void testToURL() throws Exception {
        log.info("testToURL");
        Api2DocCollector collector = new Api2DocCollector();
        ApiFolderObject folder = collector.toApiFolder(new Api2DocUtilsTestController(),
                "userController");
        ApiDocObject doc = folder.getDoc("getUser");
        String url = Api2DocUtils.toURL(doc, serverURL);
        Assert.assertEquals("http://localhost:8080/api/v1/user/123", url);

        doc = folder.getDoc("getUser2");
        url = Api2DocUtils.toURL(doc, serverURL);
        Assert.assertEquals("http://localhost:8080/api/v1/user2/0", url);

        doc = folder.getDoc("getUsers");
        url = Api2DocUtils.toURL(doc, serverURL);
        Assert.assertEquals("http://localhost:8080/api/v1/group/3/users?" +
                "age=30&city=%E5%8C%97%E4%BA%AC", url);
    }

}
