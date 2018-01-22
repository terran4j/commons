package com.terran4j.commons.hedis.dsyn;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 修饰在方法上，对这个方法在分布式环境下实现同步。<br>
 * 
 * <p>
 * 主要是使用分布式锁的原理，根据@DSynchronized(...)中 value 的设置，生成一个锁的key，然后尝试抢这个锁。<br>
 * 在多台实例并发的情况下，同一时刻只有一台实例中的一个线程才能抢到这个锁，并且在执行完后释放这把锁。<br>
 * 没有抢到锁的线程，会进入“等待”状态，直到锁释放时才会被唤醒（即使是被另一台实例的线程释放的）。<br>
 * </p>
 * 
 * <p>
 * 分布式锁，可能是 Redis 或 Zookeeper 之类的实现，具体的实现对使用者是透明的。<br>
 * 用于定义锁的 key 的这个 value 非常重要，它决定了并发冲突的范围，如果不设置，则:<br>
 * 锁的 key = 类名 + 方法名 + 所有参数类名拼接起来。<br>
 * 也就意味着所有实例的所有线程，只要执行到这个方法就会竞争同一把锁，相当于方法级别上的同步。<br>
 * </p>
 * 
 * 因此，建议精确定义 value 属性，如下所示：<br>
 * 
 * <pre class=code>
 * &#64;DSynchronized("'increment-' + #name")
 * public int incrementAndGet(&#64;Param("name") String name) {
 * 	// ...
 * }
 * </pre>
 *
 * "'increment-' + #name" 是 Spring 的 EL 表达式，#name 表示从参数 name 中取值。<br>
 * 注意，只会从打了@Param注解的参数中取值。<br>
 * 比如调用方法时参数 name = "neo"，则锁的 key 为 "increment-neo"，只有遇到同样 key 的线程才会竞争锁。<br>
 * 因此，合理定义 value 属性，可以大大减少竞争锁的情况。
 *
 * @author wei.jiang
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface DSynchronized {

	/**
	 * 定义锁的key，支持 Spring EL 表达式。
	 * 
	 * @return 锁的key生成表达式。
	 */
	String value() default "";

	/**
	 * 竞争锁等待的超时时间，以毫秒为单位。<br>
	 * 默认为5000毫秒，如果设置小于或等于 0 则表示永不超时。
	 * 
	 * @return 竞争锁的超时时间。
	 */
	long timeout() default 5000;

	/**
	 * 锁的存活时间，以毫秒为单位，超过这个时间后此锁自动释放。<br>
	 * 默认为10000毫秒，如果设置小于或等于 0 则表示永不过期。
	 * 
	 * @return 锁的存活时间。
	 */
	long keepAlive() default 10000;

}
