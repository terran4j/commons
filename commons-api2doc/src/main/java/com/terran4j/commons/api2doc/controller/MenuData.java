package com.terran4j.commons.api2doc.controller;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.util.Strings;

import java.util.List;

public class MenuData {

    private boolean folder;

    private int order = Api2Doc.DEFAULT_ORDER;

    private String id;

    private String index;

    private String name;

    private String url;

    private List<MenuData> children;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public final boolean isFolder() {
        return folder;
    }

    public final void setFolder(boolean folder) {
        this.folder = folder;
    }

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public final String getIndex() {
        return index;
    }

    public final void setIndex(String index) {
        this.index = index;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final List<MenuData> getChildren() {
        return children;
    }

    public final void setChildren(List<MenuData> children) {
        this.children = children;
    }

    public final String getUrl() {
        return url;
    }

    public final void setUrl(String url) {
        this.url = url;
    }

    public final String toString() {
        return Strings.toString(this);
    }

}
