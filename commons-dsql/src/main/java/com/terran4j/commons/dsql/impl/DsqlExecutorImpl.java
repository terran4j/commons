package com.terran4j.commons.dsql.impl;

import com.terran4j.commons.dsql.DsqlExecutor;
import com.terran4j.commons.util.Expressions;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DsqlExecutorImpl implements DsqlExecutor {

    private JdbcTemplate jdbcTemplate;

    public DsqlExecutorImpl() {
    }

    public DsqlExecutorImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按 query 查询，查询的结果，每行数据转成指定的对象。
     *
     * @param query       查询条件，必须是一个 Java Bean 对象。
     * @param elementType 返回的对象类型
     * @param sqlName     所用的 sql 文件名，必须要在 query 类的相同包下，
     *                    或在 elementType 类的相同包下，文件名为 ${sqlName}.sql.ftl 格式，
     *                    文件中可用  freemarker 语法编写 SQL， 变量引用可用 @{} 包裹起来。
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> query4List(Map<String, Object> query, final Class<T> elementType,
                                  Class<?> basePackageClass, String sqlName)
            throws BusinessException {
        SqlInfo sqlInfo = getSqlInfo(query, basePackageClass, sqlName);
        RowMapper<T> rm = CompositeBeanRowMapper.newInstance(elementType);
        List<T> result = jdbcTemplate.query(sqlInfo.getSql(), sqlInfo.getArgs(), rm);
        return result;
    }

    @Override
    public long query4Count(Map<String, Object> query, Class<?> basePackageClass, String sqlName)
            throws BusinessException{
        SqlInfo sqlInfo = getSqlInfo(query, basePackageClass, sqlName);
        Long count = jdbcTemplate.queryForObject(sqlInfo.getSql(), sqlInfo.getArgs(),
                Long.class);
        if (count == null) {
            throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                    .put("sql", sqlInfo.getSql())
                    .setMessage("Query4Count SQL must return a number, for example:\n" +
                            "select count(*) from user;\nbut SQL is: \n{sql}");
        }
        return count;
    }

    private SqlInfo getSqlInfo(Map<String, Object> query, Class<?> basePackageClass, String sqlName) throws BusinessException {
        Map<String, Object> context = buildContext(query);
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
        return info;
    }

    public static final Map<String, Object> buildContext(Map<String, Object> query) {
//        Map<String, Object> context = new HashMap<>();
//        if (query != null) {
//            context.put("query", query);
//        }
        // 暂时不需要做任何处理。
        return query;
    }

}
