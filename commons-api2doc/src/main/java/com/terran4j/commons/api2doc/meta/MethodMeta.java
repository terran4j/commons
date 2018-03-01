package com.terran4j.commons.api2doc.meta;

import java.util.ArrayList;
import java.util.List;

public class MethodMeta {

    private String id;

    private String name;

    private String comment;

    private String[] paths;

    private String[] requestMethods;

    private final List<ParamMeta> params = new ArrayList<>();

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

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public String[] getRequestMethods() {
        return requestMethods;
    }

    public void setRequestMethods(String[] requestMethods) {
        this.requestMethods = requestMethods;
    }

    public List<ParamMeta> getParams() {
        return params;
    }

    public void addParam(ParamMeta param) {
        this.params.add(param);
    }

}
