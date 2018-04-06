package com.terran4j.commons.api2doc;

import com.terran4j.commons.api2doc.impl.Api2DocObjectFactory;

import java.util.List;

public class Api2DocMocker {

    public static <T> T mockBean(Class<T> clazz) {
        return Api2DocObjectFactory.createBean(clazz);
    }

    public static <T> List<T> mockList(Class<T> clazz, int size) {
        return Api2DocObjectFactory.createList(clazz, size);
    }

    public static <T> T[] mockArray(Class<T> clazz, int size) {
        return Api2DocObjectFactory.createArray(clazz, size);
    }

}
