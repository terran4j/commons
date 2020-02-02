package com.terran4j.commons.armq.impl;

import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.common.AckMessageException;
import com.aliyun.mq.http.model.Message;
import com.terran4j.commons.armq.ConsumerConfig;
import com.terran4j.commons.armq.MessageConsumer;
import com.terran4j.commons.util.Jsons;
import com.terran4j.commons.util.task.LoopExecuteTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ConnectionClosedException;

import java.util.ArrayList;
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
            // 如果是 InterruptedException ，可能是本消费者被注解了。
            if (e instanceof InterruptedException) {
                log.warn("MessageConsumerTask was Interrupted, consumer class: {}",
                        realConsumer.getClass().getName());
                return false;
            }
            // 如果是连接被关闭了，也没办法搞了，只能撤了。
            if (e instanceof ConnectionClosedException) {
                this.stop();
                log.error("MQ Connection was Closed: {}", e.getMessage());
                return false;
            }

            // 其他情况，算成拉取消息出错。
            log.error("拉取消息出错：" + e.getMessage(), e);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                log.error("InterruptedException: " + e.getMessage());
            }
            return false; // sleep 一段时间再试。
        }
        if (messages == null || messages.size() == 0) {
            // 没有消息，sleep 一段时间再循环。
            return false;
        }
        int totalSize = messages.size();
        log.info("Received messages, size = {}", totalSize);

        List<String> handles = new ArrayList<>();
        for (Message message : messages) {
            String key = message.getMessageKey();
            String body = message.getMessageBodyString();
            log.info("Received message, key = {}, body = {}", key, body);
            T content = Jsons.toObject(body, transfer.getMessageEntityClass());
            try {
                realConsumer.onMessage(key, content);
                handles.add(message.getReceiptHandle());
            } catch (Throwable e) {
                log.error("consume message failed: " + e.getMessage(), e);
            }
        }

        int successSize = handles.size();
        if (successSize == 0) {
            // 全部消息都消费失败了，过一段时间再试。
            return false;
        } else {
            try {
                mqConsumer.ackMessage(handles);
                log.info("Ack message success.");
            } catch (AckMessageException e) {
                // 某些消息的句柄可能超时了会导致确认不成功
                AckMessageException errors = (AckMessageException) e;
                log.error("Ack message fail, requestId is: {}", errors.getRequestId());
                if (errors.getErrorMessages() != null) {
                    for (String errorHandle : errors.getErrorMessages().keySet()) {
                        log.error("Handle: {}, ErrorCode: {}, ErrorMsg: {}",
                                errorHandle, errors.getErrorMessages().get(errorHandle).getErrorCode(),
                                errors.getErrorMessages().get(errorHandle).getErrorMessage());
                    }
                }
            } catch (Throwable e) {
                log.error("Ack message fail: " + e.getMessage());
            }
        }

        return true;
    }
}
