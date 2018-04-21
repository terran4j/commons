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
    public void testToIdentify() throws Exception {
        Object[] args = new Object[]{"abc", new LinkedList<>()};
        Method method = Classes.getMethod(ClassesTest.class,
                "doAction", args, null);
        String methodId = Classes.toIdentify(method);
        String expectId = "com.terran4j.common.util.ClassesTest#doAction(java.lang.String,java.util.List)";
        Assert.assertEquals(expectId, methodId);
    }

    @Test
    public void testParseWithResultType() throws Exception {
        LinkedList<Object> list = new LinkedList<>();
        Object[] args = new Object[]{"abc", list};
        Method method = Classes.getMethod(ClassesTest.class,
                "doAction", args, null);
        Assert.assertNotNull(method);
        Assert.assertEquals(Integer.class, method.getReturnType());
    }
}
