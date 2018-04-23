package com.terran4j.commons.restpack.impl;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter<String, Date> {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Date convert(String source) {
        Date date = null;
        if (StringUtils.isEmpty(source)) {
            return date;
        }
        source = source.trim();

        try {
            long time = Long.parseLong(source);
            date = new Date(time);
            return date;
        } catch (NumberFormatException e) {
            // ignore.
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            date = sdf.parse(source);
            return date;
        } catch (ParseException e) {
            String msg = String.format("Parse Date value[%s] error, " +
                    "must be format: %s", source, DATE_FORMAT);
            throw new RuntimeException(msg);
        }
    }

}
