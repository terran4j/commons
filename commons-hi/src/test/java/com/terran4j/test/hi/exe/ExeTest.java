//package com.terran4j.test.hi.exe;
//
//import com.google.gson.JsonElement;
//import com.terran4j.commons.hi.HttpClient;
//import com.terran4j.commons.hi.Request;
//import com.terran4j.commons.hi.Response;
//import com.terran4j.commons.hi.Session;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {ExeApp.class},
//        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//public class ExeTest {
//
//    @Autowired
//    protected ApplicationContext context;
//
//    private HttpClient createClient() {
//        return HttpClient.create(ExeTest.class,
//                this.getClass().getSimpleName() + ".json", context);
//    }
//
//    @Test
//    public void testEcho() throws Exception {
//        Session session = createClient().createSession();
//        Request request = session.createRequest("echo");
//        Response response = request.param("k1", "1").param("k2", "2").exe();
//
//        String result = response.getResult().getAsString();
//        Assert.assertEquals("k1=1&k2=2", result);
//    }
//
//    @Test
//    public void testPlus() throws Exception {
//        Session session = createClient().createSession();
//        Assert.assertEquals("0", session.local("result"));
//
//        Request request = session.createRequest("plus");
//        Response response = request.input("a", "2").input("b", "3").exe();
//
//        // 用对象来接收。
//        PlusObject plusObject = response.getResult(PlusObject.class);
//        Assert.assertEquals(2, plusObject.getA());
//        Assert.assertEquals(3, plusObject.getB());
//        Assert.assertEquals(5, plusObject.getSum());
//
//        // 用 getByPath 获取结果中的数据。
//        Assert.assertEquals("5", response.getByPath("sum"));
//        Assert.assertEquals(5, response.getByPath("sum", 0));
//
//        // plus 方法将 结果写到 session 中去了。
//        Assert.assertEquals("5", session.local("result"));
//    }
//
//}
