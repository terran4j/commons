package com.terran4j.common.util;

import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.config.ConfigElement;
import com.terran4j.commons.util.config.JsonConfigElement;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class JsonConfigElementTest {

    @Data
    public static class Item {

        private int id = 0;

        private String name = "";

        public Item() {
        }

        public Item(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    public void test() throws Exception {
        String jsonText = Strings.getString(this.getClass(), "JsonConfigElementTest.json");
        ConfigElement element = new JsonConfigElement(jsonText);
        Assert.assertEquals(new Integer(33), element.attrAsInt("int"));
        Assert.assertEquals("my text", element.attr("str"));
        Assert.assertEquals(false, element.attrAsBoolean("bool"));

        Item item1 = new Item(1, "111");
        Assert.assertEquals(item1, element.attr("item1", Item.class));
        Item item2 = new Item(2, "222");
        Assert.assertEquals(item2, element.attr("item2", Item.class));

        List<Item> items = element.getChildren("items", Item.class);
        Assert.assertNotNull(items);
        Assert.assertEquals(item1, items.get(0));
        Assert.assertEquals(item2, items.get(1));
    }
}
