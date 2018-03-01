package com.terran4j.commons.api2doc.meta;

import com.terran4j.commons.api2doc.domain.ApiDataType;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;

public class ParamMeta {

    private String id;

    private String name;

    private String comment;

    private boolean required;

    private String dataType;

    private String location;

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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
