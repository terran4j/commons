package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiObject;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.error.BusinessException;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

public class ApiCommentUtils {

    public static final void setApiComment(ApiComment apiComment, ApiObject apiObject) {
        String comment = getComment(apiComment, apiObject.getId());
        if (comment != null) {
            apiObject.setComment(comment);
        }
        String sample = getSample(apiComment, apiObject.getId());
        if (sample != null) {
            apiObject.setSample(sample);
        }
    }

    public static final String getComment(ApiComment apiComment, String name) {
        if (apiComment == null) {
            return null;
        }

        String comment = apiComment.value();
        if (StringUtils.hasText(comment)) {
            return comment.trim();
        }

        Class<?> seeClass = apiComment.see();
        if (seeClass == null || seeClass == Object.class || StringUtils.isEmpty(name)) {
            return null;
        }

        Field field = Classes.getField(name, seeClass);
        if (field == null) {
            return null;
        }

        ApiComment seeComment = field.getAnnotation(ApiComment.class);
        if (seeComment == null) {
            return null;
        }

        comment = seeComment.value();
        if (StringUtils.hasText(comment)) {
            return comment.trim();
        }

        return null;
    }

    public static final String getSample(ApiComment apiComment, String name) {
        if (apiComment == null) {
            return null;
        }

        String sample = apiComment.sample();
        if (StringUtils.hasText(sample)) {
            return sample.trim();
        }

        Class<?> seeClass = apiComment.see();
        if (seeClass == null || seeClass == Object.class || StringUtils.isEmpty(name)) {
            return null;
        }

        Field field = Classes.getField(name, seeClass);
        if (field == null) {
            return null;
        }

        ApiComment seeComment = field.getAnnotation(ApiComment.class);
        if (seeComment == null) {
            return null;
        }

        sample = seeComment.sample();
        if (StringUtils.hasText(sample)) {
            return sample.trim();
        }

        return null;
    }
}
