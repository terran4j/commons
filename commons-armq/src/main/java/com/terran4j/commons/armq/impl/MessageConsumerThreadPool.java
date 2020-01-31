package com.terran4j.commons.armq.impl;

import com.terran4j.commons.armq.ConsumerConfig;
import com.terran4j.commons.armq.MessageConsumer;

/**
 * 起一组线程，以死循环的方式来拉取消息并执行。
 *
 * @param <T> 消息实例类型。
 */
public class MessageConsumerThreadPool<T> {

    private final MessageConsumer<T> consumer;

    private final Class<T> messageEntityClass;

    private final ConsumerConfig config;

    private final Thread[] threads;

    public MessageConsumerThreadPool(MessageConsumer<T> consumer,
                                     Class<T> messageEntityClass, ConsumerConfig config) {
        this.consumer = consumer;
        this.messageEntityClass = messageEntityClass;
        this.config = config;

        int threadSize = config.getThreadSize();
        if (threadSize <= 0) {
            threadSize = 1;
        }
        if (threadSize > 100) {
            threadSize = 100; // 最大不能超过 100 个线程。
        }
        threads = new Thread[threadSize];
    }

    public void start() {
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            if (thread != null) {

            }
        }
    }

}
