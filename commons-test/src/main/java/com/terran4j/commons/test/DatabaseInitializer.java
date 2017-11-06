package com.terran4j.commons.test;

import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.Strings;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 执行TestCase方法前，对数据库数据进行初始化。<br>
 * <p>
 * 1. 根据 TestCase 类（或父类）上的 @TruncateTable 注解，将相关的表清空。<br>
 * 2. 如果 @SpringBootTest 类有同包同名的 SQL 文件，将执行一下此 SQL 文件。<br>
 * 3. 如果 TestCase 类有同包同名的 SQL 文件，将执行一下此 SQL 文件。<br>
 * </p>
 * 
 * @author jiangwei
 *
 */
public class DatabaseInitializer extends BaseTestExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);

		// 如果发现有JPA的实体类，先清空对应表中的所有数据。
		Class<?> testClass = testContext.getTestClass();
		TruncateTable truncateTable = Classes.getAnnotation(testClass, TruncateTable.class);
		if (truncateTable != null && truncateTable.basePackageClass() != null) {
			Set<Package> pkgs = new HashSet<>();
			for (Class<?> basePackageClass : truncateTable.basePackageClass()) {
				Package pkg = basePackageClass.getPackage();
				if (pkgs.contains(pkg)) {
					continue;
				}
				truncateTables(basePackageClass);
				pkgs.add(pkg);
			}
		}

		// 整个 app 测试用的全局 SQL 文件。
		Class<?>[] springBootClasses = getSpringBootClasses(testContext);
		for (Class<?> springBootClass : springBootClasses) {
			exeSQLFile(springBootClass);
		}

		// 本 TestCase 类单独用的 SQL 文件。
		exeSQLFile(testClass);

		// 本 TestCase 方法单独用的 SQL 文件。
		Method testMethod = testContext.getTestMethod();
		String methodSQLName = testClass.getSimpleName() + "." + testMethod.getName() + ".sql";
		exeSQLFile(testClass, methodSQLName);
	}

	void truncateTables(Class<?> springBootClass) throws ClassNotFoundException, IOException {
		Set<Class<?>> entityClasses = Classes.scanClasses(springBootClass, Entity.class);
		if (entityClasses != null && entityClasses.size() > 0) {
			StringBuilder sqls = new StringBuilder();
			sqls.append("SET NAMES utf8mb4;\n");
			sqls.append("SET FOREIGN_KEY_CHECKS = 0;\n");
			for (Class<?> entityClass : entityClasses) {
				Entity entity = entityClass.getAnnotation(Entity.class);
				String tableName = entity.name();
				sqls.append("truncate table `").append(tableName).append("`;\n");
			}
			sqls.append("SET FOREIGN_KEY_CHECKS = 1;");
			exeSQLs(sqls.toString());
			if (log.isInfoEnabled()) {
				log.info("execute truncate table sqls:\n{}", sqls);
			}
		}
	}

	final String removeSQLComments(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return sql;
		}
		int fromIndex = 0;
		int length = sql.length();
		StringBuffer sb = new StringBuffer();
		while (fromIndex < length) {
			int startPos = sql.indexOf("--", fromIndex);
			if (startPos < 0 || startPos >= length) {
				sb.append(sql.substring(fromIndex, length));
				break;
			} else {
				sb.append(sql.substring(fromIndex, startPos));
				int endPos = sql.indexOf("\n", startPos);
				if (endPos < 0 || endPos >= length) {
					break;
				} else {
					fromIndex = endPos + 1;
				}
			}
		}
		return sb.toString().trim();
	}

	protected void exeSQLs(String sqls) {
		if (StringUtils.isEmpty(sqls)) {
			return;
		}
		String[] sqlArray = Strings.splitWithTrim(sqls, ";");
		for (String sql : sqlArray) {
			if (StringUtils.isEmpty(sql)) {
				continue;
			}
			sql = removeSQLComments(sql);
			sql = sql.trim();
			if (StringUtils.isEmpty(sql)) {
				continue;
			}

			exeSQL(sql);
		}
	}

	protected void exeSQL(String sql) {
		if (StringUtils.isEmpty(sql)) {
			return;
		}
		try {
			if (log.isInfoEnabled()) {
				log.info("prepare execute sql: {}", sql);
			}
			jdbcTemplate.execute(sql);
			if (log.isInfoEnabled()) {
				log.info("execute sql done: {}", sql);
			}
		} catch (Exception e) {
			String msg = String.format("execute sql[%s] failed: %s", sql, e.getMessage());
			log.error(msg, e);
			Assert.fail(msg);
		}
	}

	void exeSQLFile(Class<?> nameAsClass) {
		String fileName = nameAsClass.getSimpleName() + ".sql";
		exeSQLFile(nameAsClass, fileName);
	}

	void exeSQLFile(Class<?> nameAsClass, String fileName) {
		String fileContent = Strings.getString(nameAsClass, fileName);
		if (StringUtils.isEmpty(fileContent)) {
			log.warn("sql file not found or empty, package = {}, file = ", nameAsClass.getPackage().getName(),
					fileName);
			return;
		}
		exeSQLs(fileContent);
	}

}
