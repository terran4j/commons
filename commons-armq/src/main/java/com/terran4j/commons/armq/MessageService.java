package com.terran4j.commons.armq;

import com.terran4j.commons.util.error.BusinessException;

public interface MessageService {

    /**
     * 发送消息（同步调用）。
     *
     * @param content 消息对象（即消息的内容）
     * @param key     消息的key，可为空，若有请必须保证在本 Topic 是唯一标识。
     * @param tag     消息的标签，可为空。
     */
    void send(Object content, String key, String tag) throws BusinessException;

    /**
     * 注册消息消费者，会内部创建一个线程池来消费消息。<br>
     * 本方法属于极简方式，每次取1条消息，没消息时长轮询时间为 10 秒。<br>
     * 线程池核心线程数为CPU核数，最大线程数是CPU核数两倍，阻塞队列大小为 128 。<br>
     * 如果你希望根据业务情况调整这些参数，请使用方法：<br>
     * <code>registConsumer(Class<T> messageEntityClass, ConsumerConfig config)</code>
     *
     * @param messageEntityClass 消息实体类对象。
     * @param <T>                消息实体类型。
     */
    <T> void registConsumer(MessageConsumer<T> consumer,
                            Class<T> messageEntityClass) throws BusinessException;

    /**
     * @param messageEntityClass 消息实体类对象。
     * @param config             对消费者的自定义配置。
     * @param <T>                消息实体类型。
     * @see MessageService#registConsumer(MessageConsumer, Class)
     */
    <T> void registConsumer(MessageConsumer<T> consumer, Class<T> messageEntityClass,
                            ConsumerConfig config) throws BusinessException;
}
