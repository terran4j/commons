package com.terran4j.commons.api2doc.meta;

import java.util.ArrayList;
import java.util.List;

public class ClassMeta {

    private String id;

    private String name;

    private String comment;

    private final List<MethodMeta> methods = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<MethodMeta> getMethods() {
        return methods;
    }

    public void addMethod(MethodMeta method) {
        this.methods.add(method);
    }

}
