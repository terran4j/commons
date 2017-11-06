package com.terran4j.commons.dsql.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.Entity;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CompositeBeanRowMapper<T> implements RowMapper<T> {

    private static final Logger log = LoggerFactory.getLogger(CompositeBeanRowMapper.class);

    private Class<T> mappedClass;

    private BeanPropertyRowMapper<T> defaultMapper;

    private final Map<String, BeanPropertyRowMapper> mappers = new HashMap<>();

    private final Map<String, PropertyDescriptor> props = new HashMap<>();

    public static <T> CompositeBeanRowMapper<T> newInstance(Class<T> mappedClass) {
        return new CompositeBeanRowMapper<T>(mappedClass);
    }

    private CompositeBeanRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }

    private void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        defaultMapper = BeanPropertyRowMapper.newInstance(mappedClass);
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() == null) {
                continue;
            }
            Class<?> propertyType = pd.getReadMethod().getReturnType();
            Entity entity = propertyType.getAnnotation(Entity.class);
            if (entity == null) {
                continue;
            }
            String key = pd.getName();
            props.put(key, pd);

            BeanPropertyRowMapper<?> mapper = BeanPropertyRowMapper.newInstance(propertyType);
            mappers.put(key, mapper);
        }
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T result = defaultMapper.mapRow(rs, rowNum);
        if (mappers.size() > 0) {
            Iterator<String> it = mappers.keySet().iterator();
            while(it.hasNext()) {
                String key = it.next();
                BeanPropertyRowMapper<?> mapper = mappers.get(key);
                Object fieldObject = mapper.mapRow(rs, rowNum);
                PropertyDescriptor pd = props.get(key);
                Method writeMethod = pd.getWriteMethod();
                try {
                    writeMethod.invoke(result, fieldObject);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("call setter failed while map row: " + e.getMessage() + "\n" +
                            "rs: " + rs.toString() + "\n" +
                            "fieldObject: " + fieldObject + "\n" +
                            "mappedClass: " + mappedClass, e);
                }
            }
        }
        return result;
    }
}
