package com.terran4j.common.util;

import com.terran4j.commons.util.Classes;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ClassesTest {

    public Integer doAction(String name, List<Object> args) {
        return null;
    }

    @Test
    public void testParseWithResultType() throws Exception {
        LinkedList<Object> list = new LinkedList<>();
        Object[] args = new Object[]{"abc", list};
        Method method =Classes.getMethod(ClassesTest.class,
                "doAction", args, null);
        Assert.assertNotNull(method);
        Assert.assertEquals(Integer.class, method.getReturnType());
    }
}
