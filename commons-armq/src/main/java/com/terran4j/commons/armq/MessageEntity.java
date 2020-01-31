package com.terran4j.commons.armq;

import java.lang.annotation.*;

/**
 * 定义一个 Topic 的实体类：<br>
 * 此 Topic 名称即类的 SimpleName；<br>
 * 此 Topic 中的消息，均为此类的对象 JSON 序列化的文本数据(UTF-8)。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MessageEntity {

    /**
     * @return 对应的 Topic 的名称，若不写即为此类的 SimpleName 。
     */
    String topicName() default "";

    /**
     * @return 对应的 groupId，若不写在注册消息消费者时会抛错。
     */
    String groupId() default "";
}
