package com.terran4j.commons.armq.impl;

import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.model.Message;
import com.terran4j.commons.armq.ConsumerConfig;
import com.terran4j.commons.armq.MessageConsumer;
import com.terran4j.commons.util.Jsons;
import com.terran4j.commons.util.task.LoopExecuteTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MessageConsumerTask<T> extends LoopExecuteTask {

    private final MessageConsumerTransfer<T> transfer;

    public MessageConsumerTask(MessageConsumerTransfer<T> transfer) {
        super(transfer.getConfig().getThreadSleepTime());
        this.transfer = transfer;
    }

    @Override
    protected boolean execute() throws Exception {
        MQConsumer mqConsumer = transfer.getMqConsumer();
        MessageConsumer<T> realConsumer = transfer.getConsumer();
        ConsumerConfig config = transfer.getConfig();

        List<Message> messages;
        try {
            // 长轮询消费消息
            // 长轮询表示如果topic没有消息则请求会在服务端挂住3s，3s内如果有消息可以消费则立即返回
            messages = mqConsumer.consumeMessage(
                    config.getBatchSize(), // 一次最多消费3条(最多可设置为16条)
                    config.getPollingSecond() // 长轮询时间3秒（最多可设置为30秒）
            );
        } catch (Throwable e) {
            log.error("拉取消息出错：" + e.getMessage(), e);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                log.error("InterruptedException: " + e.getMessage());
            }
            return false; // sleep 一段时间再循环。
        }
        if (messages == null || messages.size() == 0) {
            return false;
        }

        for (Message message : messages) {
            String key = message.getMessageKey();
            String body = message.getMessageBodyString();
            T content = Jsons.toObject(body, transfer.getMessageEntityClass());
        }

        return true;
    }
}
