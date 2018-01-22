package com.terran4j.commons.hedis.dschedule;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 实现在分布式环境下的任务定时调度。<br>
 * 在 Spring 的<code>@Scheduled</code>进行AOP拦截，发现有别的实例在执行任务就不执行了。<br>
 * 而在执行任务的实例，会把调度控制标识，通过<code>CacheService</code>放在分布式缓存中（如：Redis）。<br>
 * <br>
 * 
 * 本注解只有写在有<code>@Scheduled</code>注解的类上才有效，有两种写法：<br>
 * 1. 写在有<code>@Scheduled</code>注解的方法上，表示此方法的要在分布式环境下调度。<br>
 * 2. 写在类上，表示此类中所有出现<code>@Scheduled</code>注解的方法都要在分布式环境下调度。<br>
 * <br>
 * 
 * TODO: 目前的实现，只考虑到任务在单实例中串行执行的情况，未考虑单实例中多线程并发执行任务的情况。<br>
 * 
 * @author wei.jiang
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface DScheduling {

	 /**
	 * 分布式调度任务的名称，唯一标识。
	 * @return
	 */
	 String value();

	/**
	 * 分布式锁的过期时间，以秒为单位，过了这个时间，锁自动释放。<br>
	 * 请根据此任务的执行时长来设置，一定要比执行时长才行。
	 * @return
	 */
	long lockExpiredSecond() default 5;
}
