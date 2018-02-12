package com.terran4j.commons.api2doc.impl;

import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.domain.ApiObject;
import com.terran4j.commons.util.Classes;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ApiCommentUtils {


    public static Class<?> getDefaultSeeClass(ApiComment apiComment,
                                              Class<?> previousSeeClass) {
        if (apiComment == null) {
            return previousSeeClass;
        }

        Class<?> defaultSeeClass = apiComment.seeClass();
        if (defaultSeeClass == Object.class) {
            return previousSeeClass;
        }

        return defaultSeeClass;
    }

    public static final void setApiComment(ApiComment apiComment,
                                           Class<?> defaultSeeClass, ApiObject apiObject) {
        String comment = getComment(apiComment, defaultSeeClass, apiObject.getId());
        if (comment != null) {
            apiObject.setComment(comment);
        }
        String sample = getSample(apiComment, defaultSeeClass, apiObject.getId());
        if (sample != null) {
            apiObject.setSample(sample);
        }
    }

//    public static final ApiComment getComment(Field field, Class<?> defaultSeeClass) {
//        ApiComment apiComment = field.getAnnotation(ApiComment.class);
//        if (apiComment !)
//    }

    public static final String getComment(ApiComment apiComment,
                                          Class<?> defaultSeeClass, String defaultName) {

        if (apiComment != null) {
            String comment = apiComment.value();
            if (StringUtils.hasText(comment)) {
                return comment.trim();
            }
        }

        ApiComment seeComment = getSeeApiComment(
                apiComment, defaultSeeClass, defaultName);
        if (seeComment == null) {
            return null;
        }

        String comment = seeComment.value();
        if (StringUtils.hasText(comment)) {
            return comment.trim();
        }

        return null;
    }

    public static final String getSample(ApiComment apiComment,
                                         Class<?> defaultSeeClass, String defaultName) {
        if (apiComment != null) {
            String sample = apiComment.sample();
            if (StringUtils.hasText(sample)) {
                return sample.trim();
            }
        }

        ApiComment seeComment = getSeeApiComment(
                apiComment, defaultSeeClass, defaultName);
        if (seeComment == null) {
            return null;
        }

        String sample = seeComment.sample();
        if (StringUtils.hasText(sample)) {
            return sample.trim();
        }

        return null;
    }

    /**
     * 获取可参照的 @ApiComment 注解对象。
     * @param apiComment
     * @param defaultSeeClass
     * @param defaultSeeField
     * @return
     */
    private static ApiComment getSeeApiComment(
            ApiComment apiComment, Class<?> defaultSeeClass, String defaultSeeField) {

        Class<?> seeClass = null;
        if (apiComment != null ) {
            seeClass = apiComment.seeClass();
        }
        if (seeClass == null || seeClass == Object.class) {
            seeClass = defaultSeeClass;
        }
        if (seeClass == null || seeClass == Object.class) {
            return null;
        }

        String seeField = null;
        if (apiComment != null) {
            seeField = apiComment.seeField();
        }
        if (StringUtils.isEmpty(seeField)) {
            seeField = defaultSeeField;
        }
        if (StringUtils.isEmpty(seeField)) {
            return null;
        }

        // 记录引用过的 seeClass， 用于循环引用检测。
        List<Class<?>> path = new ArrayList<>();
        Field field = null;
        ApiComment seeComment = null;
        while (seeClass != null) {

            // 循环引用检测。
            if (path.contains(seeClass)) {
                StringBuffer sb = new StringBuffer();
                sb.append("@ApiComment 中的 seeClass 不允许循环引用：");
                for (int i = 0; i < path.size(); i++) {
                    if (i > 0) {
                        sb.append(" --> ");
                    }
                    sb.append(seeClass.getSimpleName());
                }
                throw new RuntimeException(sb.toString());
            }
            path.add(seeClass);

            // 寻找匹配字段中的 ApiComment 。
            field = Classes.getField(seeField, seeClass);
            if (field != null) {
                seeComment = field.getAnnotation(ApiComment.class);
                if (seeComment != null) {
                    return seeComment;
                }
            }

            ApiComment parentApiComment = seeClass.getAnnotation(ApiComment.class);
            if (parentApiComment == null) {
                break;
            }
            seeClass = parentApiComment.seeClass();
        }

        return null;
    }

}
