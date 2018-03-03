package com.terran4j.test.hi.api2doc;

import com.terran4j.commons.hi.HttpClient;
import com.terran4j.commons.hi.Request;
import com.terran4j.commons.hi.Response;
import com.terran4j.commons.hi.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Api2DocApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class Api2DocSupportTest {

    @Autowired
    protected ApplicationContext context;

    private HttpClient createClient() {
        try {
            return HttpClient.createByApi2Doc(
                    "localhost", 8080, context);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return null;
        }
    }

    @Test
    public void testEcho() throws Exception {
        Session session = createClient().createSession();
        Request request = session.createRequest("test-echo");
        Response response = request.input("msg", "hello").exe();
        String result = response.getByPath("data");
        Assert.assertEquals("hello", result);
    }

    @Test
    public void testMultiply() throws Exception {
        Session session = createClient().createSession();
        Request request = session.createRequest("test-multiply");
        Response response = request.input("a", "2").input("b", "3").exe();

        // 用对象来接收。
        MultiplyObject plusObject = response.getObject(
                "data", MultiplyObject.class);
        Assert.assertEquals(2, plusObject.getA());
        Assert.assertEquals(3, plusObject.getB());
        Assert.assertEquals(6, plusObject.getResult());
    }

    @Test
    public void testPut() throws Exception {
        Session session = createClient().createSession();
        Request request = session.createRequest("test-put");
        Response response = request.input("id", "1").input("name", "abc").exe();
        String data = response.getByPath("data");
        Assert.assertEquals("1-abc", data);
    }

    @Test
    public void testDelete() throws Exception {
        Session session = createClient().createSession();
        Request request = session.createRequest("test-delete");
        Response response = request.input("id", "1").exe();
        String resultCode = response.getByPath("resultCode");
        Assert.assertEquals("success", resultCode);
    }

}