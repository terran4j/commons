package com.terran4j.commons.reflux;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于客户端，接收来自服务端的消息推送。<br>
 * 修饰在一个Spring Bean的方法上，要求此方法有且仅有一个参数，表示用此参数进行反序列化接受消息。<br>
 * 如果此方法有返回值，会将此返回值序列化成消息发给服务端。<br>
 * 也可以没有返回值。
 * 
 * @author wei.jiang
 *
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface OnMessage {

	/**
	 * 消息类型的标识符，对于同一个服务端应用，此标识符不允许重复。
	 * 
	 * @return
	 */
	String type() default "";

}
