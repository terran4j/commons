package com.terran4j.test.hi;

import com.terran4j.commons.hi.Action;
import com.terran4j.commons.hi.HttpClient;
import com.terran4j.commons.hi.Request;
import com.terran4j.commons.hi.Session;
import org.junit.Assert;
import org.junit.Test;

public class HttpClientTest extends BaseHiTest {

    /**
     * 加载 hi 的配置，测试加载到的数据是否正确。
     *
     * @throws Exception
     */
    @Test
    public void testLoadConfig() throws Exception {
        HttpClient client = create();
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
     * session 可以维持本地数据，
     * 但不同的 session 不受影响。
     *
     * @throws Exception
     */
    @Test
    public void testSessionLocal() throws Exception {
        HttpClient client = create();
        Session session = client.createSession();
        Assert.assertEquals("0", session.local("total"));

        session.local("total", "10");
        Assert.assertEquals("10", session.local("total"));

        Session session2 = client.createSession();
        Assert.assertEquals("0", session2.local("total"));
    }

    /**
     * 从 request 中获取上下文数据。
     *
     * @throws Exception
     */
    @Test
    public void testGetContextValue() throws Exception {
        HttpClient client = create();
        Session session = client.createSession();
        Request request = session.createRequest("plus");

        Assert.assertEquals("abc", request.getContextValue("token"));

        request.input("token", "123");
        Assert.assertEquals("123", request.getContextValue("token"));
    }

    /**
     * 从 request 中获取实际的 URL 及参数、Header 等信息。
     *
     * @throws Exception
     */
    @Test
    public void testRequestInput() throws Exception {
        HttpClient client = create();
        Session session = client.createSession();
        Request request = session.createRequest("plus");

        String url = "http://localhost:8080/calculator/plus";
        Assert.assertEquals(url, request.getActualURL());

        Assert.assertEquals("abc", request.getActualHeaders().get("token"));
        request.input("token", "123");
        Assert.assertEquals("123", request.getActualHeaders().get("token"));

        Assert.assertEquals("{number}", request.getActualParams().get("b"));
        request.input("number", "5");
        Assert.assertEquals("5", request.getActualParams().get("b"));
    }

    /**
     * 从 request 中获取实际的 URL 及参数、Header 等信息。
     *
     * @throws Exception
     */
    @Test
    public void testRequestContent() throws Exception {
        HttpClient client = create();
        Session session = client.createSession();
        Request request = session.createRequest("plus");

        String url = "http://localhost:8080/calculator/plus";
        Assert.assertEquals(url, request.getActualURL());

        request.content("{a=1,b=5}");

        Assert.assertEquals("{a=1,b=5}",
                request.getActualContent().toString());

    }

    /**
     * 从 request 中获取实际的 URL 及参数、Header 等信息。
     *
     * @throws Exception
     */
    @Test
    public void testSign() throws Exception {
        HttpClient client = create();
        Session session = client.createSession();
        Request request = session.createRequest("plus");
        String secretKey = "4BLYkNxktpqJSTdBf9n1IS9AQORFlqpa";
        request.input("number", "5")
                .sign("key", secretKey);
        String sign = request.getActualParams().get("key");
        Assert.assertEquals("8f026a25a4fed07a3003d8850e943ca1", sign);
    }
}