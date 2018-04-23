package com.terran4j.test.restpack;

import com.terran4j.commons.restpack.EnableRestPack;
import com.terran4j.commons.restpack.HttpResult;
import com.terran4j.commons.restpack.impl.HttpResultMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

@SpringBootTest(
        classes = {NoHttpResultConverterTest.RestPackApp.class}
)
@RunWith(SpringJUnit4ClassRunner.class)
public class NoHttpResultConverterTest {

    @EnableRestPack
    @SpringBootApplication
    public static class RestPackApp {
    }

    @Autowired
    private HttpResultMapper httpResultMapper;

    @Test
    public void testConvertWithSuccess() throws Exception {
        Object data = new String[]{"pig", "dog", "cat"};
        HttpResult httpResult = HttpResult.successFully(data);
        Object result = httpResultMapper.convert(httpResult);
        if (result instanceof Map) {
            Map<String, Object> myHttpResult = (Map<String, Object>) result;
            Assert.assertEquals(data, myHttpResult.get("result"));
        } else {
            Assert.fail("Object result should be Map<String, Object>, but is: "
                    + result.getClass().getName());
        }
    }
}
