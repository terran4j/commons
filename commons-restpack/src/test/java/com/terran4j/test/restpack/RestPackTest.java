package com.terran4j.test.restpack;

import com.terran4j.commons.restpack.RestPackIgnore;
import com.terran4j.commons.restpack.RestPackUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
public class RestPackTest {

    private static final Logger log = LoggerFactory.getLogger(RestPackTest.class);

    public static class WrapTypeBean {

        @RestPackIgnore
        private Boolean value = true;

        public WrapTypeBean() {
        }

        public WrapTypeBean(Boolean value) {
            this.value = value;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
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


    public static class ListBean {

        @RestPackIgnore
        private Integer value = 1;

        private List<WrapTypeBean> beans = new ArrayList<>();

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public List<WrapTypeBean> getBeans() {
            return beans;
        }

        public void setBeans(List<WrapTypeBean> beans) {
            this.beans = beans;
        }
    }

    @Test
    public void testClearIgnoreFieldOfList() throws Exception {
        log.info("testClearIgnoreFieldOfList begin.");
        ListBean bean = new ListBean();
        bean.getBeans().add(new WrapTypeBean(true));
        bean.getBeans().add(new WrapTypeBean(false));
        RestPackUtils.clearIgnoreFields(bean);
        Assert.assertNull(bean.getValue());
        Assert.assertEquals(2, bean.getBeans().size());
        Assert.assertNull(bean.getBeans().get(0).getValue());
        Assert.assertNull(bean.getBeans().get(1).getValue());
        log.info("testClearIgnoreFieldOfList end.");
    }

    public static class MapBean {

        @RestPackIgnore
        private Integer value = 1;

        private Map<String, WrapTypeBean> beans = new HashMap<>();

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Map<String, WrapTypeBean> getBeans() {
            return beans;
        }

        public void setBeans(Map<String, WrapTypeBean> beans) {
            this.beans = beans;
        }
    }

    @Test
    public void testClearIgnoreFieldOfMap() throws Exception {
        log.info("testClearIgnoreFieldOfMap begin.");
        MapBean bean = new MapBean();
        bean.getBeans().put("a", new WrapTypeBean(true));
        bean.getBeans().put("b", new WrapTypeBean(false));
        RestPackUtils.clearIgnoreFields(bean);
        Assert.assertNull(bean.getValue());
        Assert.assertEquals(2, bean.getBeans().size());
        Assert.assertNull(bean.getBeans().get("a").getValue());
        Assert.assertNull(bean.getBeans().get("b").getValue());
        log.info("testClearIgnoreFieldOfMap end.");
    }

}
