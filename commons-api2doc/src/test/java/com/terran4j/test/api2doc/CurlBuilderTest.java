package com.terran4j.test.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.impl.Api2DocCollector;
import com.terran4j.commons.api2doc.impl.CurlBuilder;
import com.terran4j.commons.util.error.BusinessException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class CurlBuilderTest {

    private String serverURL = "http://localhost:8080";

    private Api2DocCollector collector = new Api2DocCollector();

    @Api2Doc(id = "curl")
    @RestController
    @RequestMapping(value = "/api/v1/curl")
    public class CurlBuilderTestController {

        @RequestMapping(value = "/normal/{p1}/abc/{p2}",
                method = RequestMethod.POST)
        public void simple(
                @PathVariable("p1") Long p1,
                @PathVariable("p2") String p2,
                @RequestParam("myParam") String myParam,
                @RequestHeader("myHeader") String myHeader,
                @CookieValue("myCookie") String myCookie) {
        }

        @RequestMapping(value = "/comment/{p1}/{p2}",
                method = RequestMethod.POST)
        public void withComment(
                @ApiComment(value = "路径参数", sample = "123")
                @PathVariable("p1") Long p1,

                @ApiComment(value = "路径参数", sample = "abc")
                @PathVariable("p2") String p2,

                @ApiComment(value = "请求参数", sample = "k123")
                @RequestParam("k1") String k1,

                @ApiComment(value = "Header参数", sample = "false")
                @RequestHeader("k2") boolean k2,

                @ApiComment(value = "Cookie参数", sample = "5.86")
                @CookieValue("k3") double k3) {
        }

        @RequestMapping(value = "/getting", method = RequestMethod.GET)
        public void withGet(
                @ApiComment(sample = "键1") String k1,
                @ApiComment(sample = "键2") String k2) {
        }
    }


    private ApiDocObject loadDoc(String methodName) throws BusinessException {
        ApiFolderObject folder = collector.toApiFolder(
                new CurlBuilderTest.CurlBuilderTestController(),
                "curlBuilderTestController");
        ApiDocObject doc = folder.getDoc(methodName);
        Assert.assertNotNull(doc);
        return doc;
    }

    @Test
    public void testToCurlWithGet() throws Exception {
        ApiDocObject doc = loadDoc("withGet");
        String curl = CurlBuilder.toCurl(doc, serverURL);
        Assert.assertEquals(
                "curl \"http://localhost:8080/api/v1/curl/getting" +
                        "?k1=%E9%94%AE1&k2=%E9%94%AE2\"",
                curl);
    }

    @Test
    public void testToCurlSimple() throws Exception {
        ApiDocObject doc = loadDoc("simple");
        String curl = CurlBuilder.toCurl(doc, serverURL);
        Assert.assertEquals(
                "curl -H \"myHeader: myHeader\"" +
                        " -d \"myParam=myParam\"" +
                        " \"http://localhost:8080/api/v1/curl/normal/0/abc/p2\"",
                curl);
    }

    @Test
    public void testToCurlWithComment() throws Exception {
        ApiDocObject doc = loadDoc("withComment");
        String curl = CurlBuilder.toCurl(doc, serverURL);
        Assert.assertEquals(
                "curl -H \"k2: false\"" +
                        " -d \"k1=k123\"" +
                        " \"http://localhost:8080/api/v1/curl/comment/123/abc\"",
                curl);
    }

}
