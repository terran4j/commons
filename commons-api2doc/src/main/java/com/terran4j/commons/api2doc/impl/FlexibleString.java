package com.terran4j.commons.api2doc.impl;

/**
 * 将空串视为 null
 * 能转成 html 格式。
 */
public class FlexibleString {

    private StringBuilder value = new StringBuilder();

    public FlexibleString() {
    }

    public FlexibleString(String value) {
        if (value != null) {
            this.value.append(value);
        }
    }

    public String getValue() {
        if (value.length() == 0) {
            return null;
        }
        return value.toString();
    }

    public void setValue(String value) {
        if (value == null) {
            this.value = new StringBuilder();
        } else {
            this.value = new StringBuilder(value);
        }
    }

    public FlexibleString append(String appendValue) {
        if (appendValue != null) {
            value.append(appendValue);
        }
        return this;
    }

    public FlexibleString insertLine(String insertValue) {
        if (insertValue != null) {
            if (this.value.length() > 0) {
                this.value.insert(0, insertValue + "\n");
            } else {
                this.value.insert(0, insertValue);
            }
        }
        return this;
    }

    public String html() {
        if (value.length() == 0) {
            return null;
        }
        String replacement = "<br/>";
        return value.toString().replaceAll("\n", replacement);
    }

    public String javadoc(int indent) {
        if (value.length() == 0) {
            return null;
        }
        String replacement = "";
        for (int i = 0; i < indent; i++) {
            replacement += "    ";
        }
        replacement = "<br/>\n" + replacement + (" * ");
        return value.toString().replaceAll("\n", replacement);
    }

    @Override
    public String toString() {
        if (value.length() == 0) {
            return "";
        }
        return value.toString();
    }
}
