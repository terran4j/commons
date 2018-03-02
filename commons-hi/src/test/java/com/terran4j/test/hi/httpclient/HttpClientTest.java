package com.terran4j.test.hi.httpclient;

import com.terran4j.commons.hi.Action;
import com.terran4j.commons.hi.HttpClient;
import com.terran4j.commons.hi.Request;
import com.terran4j.commons.hi.Session;
import com.terran4j.demo.hi.HttpClientApp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {HttpClientApp.class},
        webEnvironment = WebEnvironment.DEFINED_PORT)
public class HttpClientTest {

    @Autowired
    protected ApplicationContext context;

    private HttpClient create(String name) {
        return HttpClient.create(HttpClientTest.class,
                name + ".json", context);
    }

    /**
     * 加载 hi 的配置，测试加载到的数据是否正确。
     *
     * @throws Exception
     */
    @Test
    public void testLoadConfig() throws Exception {
        HttpClient client = create("demo");
        Assert.assertEquals("0", client.local("total"));

        Action plusAction = client.getActions().get("plus");
        Assert.assertEquals("plus", plusAction.getId());
        Assert.assertEquals("两数相加", plusAction.getName());
        Assert.assertEquals("/calculator/plus", plusAction.getUrl());
        Assert.assertEquals("POST", plusAction.getMethod());

        Assert.assertEquals("{total}", plusAction.param("a"));
        Assert.assertEquals("{number}", plusAction.param("b"));

        Assert.assertEquals("{token}", plusAction.header("token"));
    }

    /**
     *  session 可以维持本地数据，
     *  但不同的 session 不受影响。
     * @throws Exception
     */
    @Test
    public void testSessionLocal() throws Exception {
        HttpClient client = create("demo");
        Session session = client.create();
        Assert.assertEquals("0", session.local("total"));

        session.local("total", "10");
        Assert.assertEquals("10", session.local("total"));

        Session session2 = client.create();
        Assert.assertEquals("0", session2.local("total"));
    }

    /**
     *  从 request 中获取实际的 URL 及参数。
     * @throws Exception
     */
    @Test
    public void testRequest() throws Exception {
        HttpClient client = create("demo");
        Session session = client.create();
        Request request = session.createRequest("plus");

        String url = "http://localhost:8080/calculator/plus";
        Assert.assertEquals(url, request.getActualURL());

        request.input("number", "5");
        Assert.assertEquals("5", request.getActualParams().get("b"));
    }
}
