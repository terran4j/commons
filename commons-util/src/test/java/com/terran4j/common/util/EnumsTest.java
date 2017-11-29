package com.terran4j.common.util;

import com.terran4j.commons.util.Enums;
import org.junit.Assert;
import org.junit.Test;

public class EnumsTest {

    public enum UserType {

        root,

        admin,

        user,

    }

    @Test
    public void testGetEnumObject() throws Throwable {
        Object root = Enums.getEnumObject(UserType.class, "root");
        Assert.assertEquals(UserType.root, root);
        Object user = Enums.getEnumObject(UserType.class, "user");
        Assert.assertEquals(UserType.user, user);
    }
}
