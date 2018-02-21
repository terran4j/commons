package com.terran4j.test.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.api2doc.domain.ApiResultObject;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class ParseApiCommentOnSeeClassLoop {

    private static final Logger log = LoggerFactory.getLogger(
            ParseApiCommentOnSeeClassLoop.class);

    public class Comments {

        @ApiComment(value = "用户id", sample = "123")
        private Long id;

        @ApiComment(value = "用户名称", sample = "neo")
        private Long name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getName() {
            return name;
        }

        public void setName(Long name) {
            this.name = name;
        }
    }

    @ApiComment(seeClass = Comments.class)
    public class User {

        private Long id;

        private Long name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getName() {
            return name;
        }

        public void setName(Long name) {
            this.name = name;
        }
    }

    @Api2Doc
    @ApiComment(seeClass = User.class)
    @RestController
    @RequestMapping(value = "/test/api2doc/seeClass")
    public static class MyController {

        @RequestMapping(value = "/user/{id}")
        public User updateUser(
                @PathVariable("id") Long id,
                @RequestParam("name") String name) {
            return null;
        }
    }

    @Test
    public void testParseApiCommentOnSeeClassLoop() throws Exception {
        log.info("testParseApiCommentOnSeeClass");

        Api2DocCollector collector = new Api2DocCollector();
        ApiFolderObject folder = collector.toApiFolder(
                new ParseApiCommentOnSeeClassLoop.MyController(), "myController");
        ApiDocObject doc = folder.getDoc("updateUser");

        List<ApiParamObject> params = doc.getParams();
        Assert.assertEquals("用户id", params.get(0).getComment().toString());
        Assert.assertEquals("123", params.get(0).getSample().toString());
        Assert.assertEquals("用户名称", params.get(1).getComment().toString());
        Assert.assertEquals("neo", params.get(1).getSample().toString());

        ApiResultObject user = doc.getResults().get(0);

        ApiResultObject userId = user.getChildren().get(0);
        Assert.assertEquals("id", userId.getId());
        Assert.assertEquals("用户id", userId.getComment().getValue());
        Assert.assertEquals("123", userId.getSample().getValue());

        ApiResultObject userName = user.getChildren().get(1);
        Assert.assertEquals("name", userName.getId());
        Assert.assertEquals("用户名称", userName.getComment().getValue());
        Assert.assertEquals("neo", userName.getSample().getValue());
    }

}
