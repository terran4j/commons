package com.terran4j.test.hi.exe;

import com.terran4j.commons.hi.Request;
import com.terran4j.commons.hi.Response;
import com.terran4j.commons.hi.Session;
import com.terran4j.commons.util.config.ConfigElement;
import com.terran4j.test.hi.BaseHiTest;
import org.junit.Assert;
import org.junit.Test;

public class ExeTest extends BaseHiTest {

    @Test
    public void testEcho() throws Exception {
        Session session = create().createSession();
        Request request = session.createRequest("echo");
        Response response = request.param("k1", "1").param("k2", "2").exe();

        String result = response.getResult().getValue();
        Assert.assertEquals("k1=1&k2=2", result);
    }

    @Test
    public void testPlus() throws Exception {
        Session session = create().createSession();
        Assert.assertEquals("0", session.local("result"));

        Request request = session.createRequest("plus");
        Response response = request.input("a", "2").input("b", "3").exe();
        ConfigElement result = response.getResult();

        // 用对象来接收。
        PlusObject plusObject = result.asObject(PlusObject.class);
        Assert.assertEquals(2, plusObject.getA());
        Assert.assertEquals(3, plusObject.getB());
        Assert.assertEquals(5, plusObject.getSum());

        // 用 getByPath 获取结果中的数据。
        Assert.assertEquals(5, result.attr("sum", 0));

        // plus 方法将 结果写到 session 中去了。
        Assert.assertEquals("5", session.local("result"));
    }

}