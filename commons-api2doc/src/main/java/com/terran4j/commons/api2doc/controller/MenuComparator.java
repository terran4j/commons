package com.terran4j.commons.api2doc.controller;

import java.util.Comparator;

public class MenuComparator implements Comparator<MenuData> {

    @Override
    public int compare(MenuData o1, MenuData o2) {
        if (o1 == null || o2 == null) {
            throw new NullPointerException();
        }
        if (o1.getOrder() < o2.getOrder()) {
            return -1;
        }
        if (o1.getOrder() > o2.getOrder()) {
            return 1;
        }
        return 0;
    }

}
