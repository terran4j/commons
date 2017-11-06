package com.terran4j.commons.api2;

import java.lang.annotation.*;

/**
 * 用于补充 Api 接口各种元素的信息，包括 Api 请求、入参、返回值字段等。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ApiInfo {

    /**
     * API元素的id。<br>
     * 对于一个类中的重载方法，一定要用不同的 id 区分，不然会出错。
     *
     * @return
     */
    String id() default "";

    /**
     * 是否忽略此API元素。
     *
     * @return
     */
    boolean ignore() default false;

    /**
     * 设置API子元素的排序。
     *
     * @return
     */
    String[] orders() default {};

    /**
     * API元素的名称。
     *
     * @return
     */
    String name() default "";

    /**
     * API元素的描述，通常是面向用户的说明。
     *
     * @return
     */
    String desc() default "";

}
