package com.terran4j.test.commons.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import com.terran4j.commons.api2doc.impl.ApiCommentUtils;
import com.terran4j.commons.util.Classes;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.List;

public class ApiCommentUtilsTest {

    public class MyObject {

        @ApiComment(value = "用户id", sample = "123")
        private Long id;

        @ApiComment(value = "用户名", sample = "terran4j")
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


    @Api2Doc
    @RestController
    @RequestMapping(value = "/api/v1/demo")
    public static class MyController {

        @RequestMapping(value = "/user/{userId}")
        public void updateUser(
                @ApiComment(seeClass = MyObject.class, seeField = "id")
                @PathVariable("userId") Long userId,
                @ApiComment(seeClass = MyObject.class)
                @RequestParam("name") Integer userName) {
        }
    }

    @Test
    public void testSee() throws Exception {
        Api2DocCollector collector = new Api2DocCollector();
        ApiFolderObject folder = collector.toApiFolder(new MyController(),"myController");
        ApiDocObject doc = folder.getDoc("updateUser");
        List<ApiParamObject> params = doc.getParams();
        Assert.assertEquals("用户id", params.get(0).getComment().toString());
        Assert.assertEquals("123", params.get(0).getSample().toString());
        Assert.assertEquals("用户名", params.get(1).getComment().toString());
        Assert.assertEquals("terran4j", params.get(1).getSample().toString());
    }

    public class AnotherObject {

        @ApiComment(seeClass = MyObject.class)
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testGetComment() throws Exception {
        String fieldName = "name";
        Field field = Classes.getField(fieldName, AnotherObject.class);
        ApiComment apiComment = field.getAnnotation(ApiComment.class);
        String comment = ApiCommentUtils.getComment(apiComment, fieldName);
        Assert.assertEquals("用户名", comment);
        String sample = ApiCommentUtils.getSample(apiComment, fieldName);
        Assert.assertEquals("terran4j", sample);
    }

}
