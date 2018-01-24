package com.terran4j.commons.dsql;

import com.terran4j.commons.util.error.BusinessException;

import java.util.List;
import java.util.Map;

/**
 * 动态 SQL 执行器。
 * @author jiangwei
 */
public interface DsqlExecutor {

    /**
     * 按 query 查询，查询的结果，每行数据转成指定的对象。
     *
     * @param query            查询条件。
     * @param elementType      返回的对象类型
     * @param basePackageClass  sql 文件所在的包中的类对象。
     * @param sqlName          所用的 sql 文件名，必须要在 query 类的相同包下，
     *                         或在 elementType 类的相同包下，文件名为 ${sqlName}.sql.ftl 格式，
     *                         文件中可用  freemarker 语法编写 SQL， 变量引用可用 @{} 包裹起来。
     * @param <T> 返回类型
     * @return 查询记录对应的对象列表。
     */
    <T> List<T> query4List(Map<String, Object> query, Class<T> elementType,
                           Class<?> basePackageClass, String sqlName)
            throws BusinessException;

    /**
     *
     * @param query 查询条件。
     * @param basePackageClass sql 文件所在的包中的类对象。
     * @param sqlName 所用的 sql 文件名，必须要在 query 类的相同包下，
     *                         或在 elementType 类的相同包下，文件名为 ${sqlName}.sql.ftl 格式，
     *                         文件中可用  freemarker 语法编写 SQL， 变量引用可用 @{} 包裹起来。
     * @return 查询记录数量。
     * @throws BusinessException 查询出错。
     */
    long query4Count(Map<String, Object> query,
                     Class<?> basePackageClass, String sqlName)
            throws BusinessException;
}
