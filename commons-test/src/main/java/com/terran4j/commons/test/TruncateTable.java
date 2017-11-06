package com.terran4j.commons.test;

import java.lang.annotation.*;

/**
 * 清空指定的表的数据。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TruncateTable {

	/**
	 *
	 * @return
	 */
	Class<?>[] basePackageClass() default {};

}