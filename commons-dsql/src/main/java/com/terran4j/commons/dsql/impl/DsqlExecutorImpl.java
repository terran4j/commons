package com.terran4j.commons.dsql.impl;

import com.terran4j.commons.dsql.DsqlExecutor;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

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
     * @param sqlInfo       查询用的 SQL 及参数。
     * @param elementType 返回的对象类型
     * @param <T> 返回的对象类型
     * @return 将结果集转化成对象列表
     */
    @Override
    public <T> List<T> query4List(SqlInfo sqlInfo, Class<T> elementType) {
        RowMapper<T> rm = CompositeBeanRowMapper.newInstance(elementType);
        List<T> result = jdbcTemplate.query(sqlInfo.getSql(), sqlInfo.getArgs(), rm);
        return result;
    }

    @Override
    public int query4Count(SqlInfo sqlInfo) throws BusinessException {
        Integer count = jdbcTemplate.queryForObject(sqlInfo.getSql(), sqlInfo.getArgs(),
                Integer.class);
        if (count == null) {
            throw new BusinessException(ErrorCodes.CONFIG_ERROR)
                    .put("sql", sqlInfo.getSql())
                    .setMessage("Query4Count SQL must return a number, for example:\n" +
                            "select count(*) from user;\nbut SQL is: \n{sql}");
        }
        return count;
    }

    @Override
    public int update(SqlInfo sqlInfo) throws BusinessException {
        return jdbcTemplate.update(sqlInfo.getSql(), sqlInfo.getArgs());
    }


}
