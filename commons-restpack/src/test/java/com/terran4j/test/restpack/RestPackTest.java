package com.terran4j.test.restpack;

import com.terran4j.commons.restpack.RestPackIgnore;
import com.terran4j.commons.restpack.RestPackUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class RestPackTest {

    private static final Logger log = LoggerFactory.getLogger(RestPackTest.class);

    public static class WrapTypeBean {

        @RestPackIgnore
        private Boolean value = true;

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }

    }

    public static class BasicTypeBean {

        @RestPackIgnore
        private boolean value = true;

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }

    }

    @Test
    public void testClearIgnoreFieldOfWrapType() throws Exception {
        log.info("testClearIgnoreFieldOfWrapType");
        WrapTypeBean bean = new WrapTypeBean();
        Assert.assertTrue(bean.getValue());
        RestPackUtils.clearIgnoreFields(bean);
        Assert.assertNull(bean.getValue());
    }

    @Test
    public void testClearIgnoreFieldOfBasicType() throws Exception {
        log.info("testClearIgnoreFieldOfBasicType");
        BasicTypeBean bean = new BasicTypeBean();
        Assert.assertTrue(bean.getValue());
        try {
            RestPackUtils.clearIgnoreFields(bean);
            Assert.fail("应该出错！");
        }catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("不允许修饰在基本类型字段上"));
        }
    }
}
