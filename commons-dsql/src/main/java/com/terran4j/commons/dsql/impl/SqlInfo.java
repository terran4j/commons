package com.terran4j.commons.dsql.impl;

import com.terran4j.commons.util.Expressions;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SqlInfo {

    private static final Logger log = LoggerFactory.getLogger(DsqlExecutorImpl.class);

    private String sql;

    private Object[] args;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "SqlInfo{" +
                "sql='" + sql + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    public static SqlInfo create(Map<String, Object> context, Class<?> basePackageClass,
                                 String sqlName) throws BusinessException {
        DsqlBuilder dsqlBuilder = DsqlBuilder.getInstance();
        String sql = dsqlBuilder.buildSQL(context, basePackageClass, sqlName);

        List<String> keys = new ArrayList<>();
        sql = dsqlBuilder.buildPreparedArgs(sql, keys);

        Object[] args = new Object[keys.size()];
        int i = 0;
        for (String key : keys) {
            String el = "#" + key;
            Object value = Expressions.parse(el, context);
            args[i] = value;
            i++;
        }

        SqlInfo info = new SqlInfo();
        info.setArgs(args);
        info.setSql(sql);
        if (log.isInfoEnabled()) {
            log.info("\nSQL（变量替换后）: \n{}\n参数: {}", sql.trim(), Strings.toString(args));
        }
        return info;
    }

}
