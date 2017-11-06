package com.terran4j.commons.api2doc.domain;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.util.Strings;

public class ApiObject {

    private String id;

    private String name;

    private String comment;

    private String sample;

    private int order = Api2Doc.DEFAULT_ORDER;

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

    public final int getOrder() {
        return order;
    }

    public final void setOrder(int order) {
        this.order = order;
    }

    public void insertComment(String comment) {
        if (comment == null) {
            return;
        }
        if (this.comment == null) {
            this.comment = comment;
        } else {
            this.comment = comment + "<br/>" + this.comment;
        }
    }

    public final String getSample() {
        return sample;
    }

    public final void setSample(String sample) {
        this.sample = sample;
    }

    @Override
    public final String toString() {
        return Strings.toString(this);
    }

}