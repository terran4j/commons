package com.terran4j.test.restpack;

import com.terran4j.commons.restpack.EnableRestPack;
import com.terran4j.commons.restpack.HttpResult;
import com.terran4j.commons.restpack.HttpResultConverter;
import com.terran4j.commons.restpack.HttpResultMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(
        classes = {HttpResultConverterTest.RestPackApp.class}
)
@RunWith(SpringJUnit4ClassRunner.class)
public class HttpResultConverterTest {

    public static class MyHttpResult {

        private Object data;

        private String code;

        private String msg;

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public static class MyHttpResultConverter implements HttpResultConverter {

        @Override
        public Object convert(HttpResult httpResult) {
            MyHttpResult myResult = new MyHttpResult();
            myResult.setCode(httpResult.getResultCode());
            myResult.setData(httpResult.getData());
            myResult.setMsg(httpResult.getMessage());
            return myResult;
        }
    }

    @EnableRestPack
    @SpringBootApplication
    public static class RestPackApp {

        @Bean
        public HttpResultConverter httpResultConverter() {
            return new MyHttpResultConverter();
        }
    }

    @Autowired
    private HttpResultMapper httpResultMapper;

    @Test
    public void testConvertWithSuccess() throws Exception {
        Object data = new String[]{"pig", "dog", "cat"};
        HttpResult httpResult = HttpResult.successFully(data);
        Object result = httpResultMapper.convert(httpResult);
        if (result instanceof MyHttpResult) {
            MyHttpResult myHttpResult = (MyHttpResult) result;
            Assert.assertEquals(data, myHttpResult.getData());
        } else {
            Assert.fail("Object result should be MyHttpResult, but is: "
                    + result.getClass().getName());
        }
    }
}
