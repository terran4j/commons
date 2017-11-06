package com.terran4j.commons.dsql;

import com.terran4j.commons.util.Strings;

public class QueryBean {

    public static final String wrapWithLike(String source) {
        if (source == null) {
            return null;
        }
        source = source.trim();
        if (source.length() == 0) {
            return "%";
        }
        if (source.indexOf("%") >= 0) {
            return source;
        }
        if (!source.startsWith("%")) {
            source = "%" + source;
        }
        if (!source.endsWith("%")) {
            source =  source + "%";
        }
        return source;
    }

    public String toString() {
        return Strings.toString(this);
    }
}
