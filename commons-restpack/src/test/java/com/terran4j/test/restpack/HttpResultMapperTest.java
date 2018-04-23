package com.terran4j.test.restpack;

import com.terran4j.commons.restpack.EnableRestPack;
import com.terran4j.commons.restpack.HttpResult;
import com.terran4j.commons.restpack.impl.HttpResultMapper;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(
        classes = {HttpResultMapperTest.RestPackApp.class}
)
@RunWith(SpringJUnit4ClassRunner.class)
public class HttpResultMapperTest {

    private static final Logger log = LoggerFactory.getLogger(HttpResultMapperTest.class);

    @EnableRestPack
    @SpringBootApplication
    public static class RestPackApp {
    }

    @Autowired
    private HttpResultMapper httpResultMapper;

    @Test
    public void testToMapWithSuccess() throws Exception {
        log.info("testToMapWithSuccess");

        Object data = new String[]{"pig", "dog", "cat"};
        HttpResult httpResult = HttpResult.successFully(data);
        Map<String, Object> map = httpResultMapper.toMap(httpResult);

        Assert.assertNotNull(map.get("requestCode"));
        Assert.assertNull(map.get("requestId"));
        Assert.assertNotNull(map.get("currentTime"));
        Assert.assertNull(map.get("serverTime"));
        Assert.assertNotNull(map.get("spend"));
        Assert.assertNull(map.get("spendTime"));

        Assert.assertEquals(data, map.get("result"));
        Assert.assertNull(map.get("data"));
        Assert.assertEquals("OK", map.get("status"));
        Assert.assertNull(map.get("resultCode"));
    }

    @Test
    public void testToMapWithFailure() throws Exception {
        log.info("testToMapWithFailure");

        String msg = "key is null";
        BusinessException be = new BusinessException(ErrorCodes.NULL_PARAM)
                .setMessage(msg).put("key", "k1");
        HttpResult httpResult = HttpResult.fail(be);
        Map<String, Object> map = httpResultMapper.toMap(httpResult);

        Assert.assertEquals(ErrorCodes.NULL_PARAM, map.get("status"));
        Assert.assertNull(map.get("resultCode"));

        Assert.assertEquals(msg, map.get("msg"));
        Assert.assertNull(map.get("message"));

        Map<String, Object> props = new HashMap<>();
        props.put("key", "k1");
        Assert.assertEquals(props, map.get("data"));
        Assert.assertNull(map.get("props"));
    }

}
