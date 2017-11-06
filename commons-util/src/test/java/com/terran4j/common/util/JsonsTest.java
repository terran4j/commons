package com.terran4j.common.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terran4j.commons.util.Jsons;

@RunWith(SpringJUnit4ClassRunner.class)
public class JsonsTest {

    private static final Logger log = LoggerFactory.getLogger(JsonsTest.class);

    @Test
    public void testJsonFormat() throws Exception {
        log.info("testJsonFormat");
        String uglyJsonText = "{\"data1\":100,\"data2\":\"hello\",\"list\":[\"String 1\",\"String 2\",\"String 3\"]}";
        String prettyJsonText = Jsons.format(uglyJsonText);
        log.info("JSON格式化前：\n{}", uglyJsonText);
        log.info("JSON格式化后：\n{}", prettyJsonText);
        String exceptedJsonText = "{\n" + "  \"data1\": 100,\n" + "  \"data2\": \"hello\",\n" + "  \"list\": [\n"
                + "    \"String 1\",\n" + "    \"String 2\",\n" + "    \"String 3\"\n" + "  ]\n" + "}";
        Assert.assertEquals(exceptedJsonText, prettyJsonText.trim());
    }
}
