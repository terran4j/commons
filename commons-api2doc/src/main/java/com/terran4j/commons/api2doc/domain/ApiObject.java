package com.terran4j.commons.api2doc.domain;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.impl.FlexibleString;
import com.terran4j.commons.util.Strings;

public class ApiObject {

    private String id;

    private String name;

    private final FlexibleString comment = new FlexibleString();

    private final FlexibleString sample = new FlexibleString();

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

    public final int getOrder() {
        return order;
    }

    public final void setOrder(int order) {
        this.order = order;
    }

    public void insertComment(String comment) {
        this.comment.insertLine(comment);
    }

    public FlexibleString getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment.setValue(comment);
    }

    public FlexibleString getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample.setValue(sample);
    }

    @Override
    public final String toString() {
        return Strings.toString(this);
    }

}