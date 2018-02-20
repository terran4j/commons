package com.terran4j.commons.dsql;

import com.terran4j.commons.dsql.impl.SqlInfo;
import com.terran4j.commons.util.error.BusinessException;

import java.util.List;

/**
 * 动态 SQL 执行器。
 *
 * @author jiangwei
 */
public interface DsqlExecutor {

    /**
     * 按 query 查询，查询的结果，每行数据转成指定的对象。
     *
     * @param sqlInfo     SQL 及参数。
     * @param elementType 返回的对象类型
     * @param <T>         返回类型
     * @return 查询记录对应的对象列表。
     */
    <T> List<T> query4List(SqlInfo sqlInfo, Class<T> elementType)
            throws BusinessException;

    /**
     * @param sqlInfo SQL 及参数
     * @return 查询记录数量。
     * @throws BusinessException 查询出错。
     */
    int query4Count(SqlInfo sqlInfo) throws BusinessException;

    /**
     * @param sqlInfo SQL 及参数
     * @return 受影响记录的数量。
     * @throws BusinessException 执行出错。
     */
    int update(SqlInfo sqlInfo) throws BusinessException;
}
