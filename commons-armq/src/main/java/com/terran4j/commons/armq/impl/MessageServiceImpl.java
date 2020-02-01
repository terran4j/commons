package com.terran4j.commons.armq.impl;

import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.terran4j.commons.armq.ConsumerConfig;
import com.terran4j.commons.armq.MessageConsumer;
import com.terran4j.commons.armq.MessageEntity;
import com.terran4j.commons.armq.MessageService;
import com.terran4j.commons.util.Jsons;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MessageServiceImpl implements MessageService {

    private final Map<Class<?>, MQProducer> producers = new ConcurrentHashMap<>();

    private final Map<MessageConsumer<?>, MessageConsumerTransfer<?>>
            consumers = new ConcurrentHashMap<>();

    private final String instanceId;

    private final MQClient mqClient;

    public MessageServiceImpl(String instanceId, MQClient mqClient) {
        this.instanceId = instanceId;
        this.mqClient = mqClient;
    }

    private MQProducer getOrCreateProducer(Class<?> messageEntityClass) throws BusinessException {
        MQProducer producer = producers.get(messageEntityClass);
        if (producer != null) {
            return producer;
        }

        synchronized (messageEntityClass) {
            producer = producers.get(messageEntityClass);
            if (producer != null) {
                return producer;
            }

            MessageEntity messageEntity = getMessageEntity(messageEntityClass);
            String topicName = getTopicName(messageEntity, messageEntityClass);
            producer = mqClient.getProducer(instanceId, topicName);
            producers.put(messageEntityClass, producer);
            return producer;
        }
    }

    private static MessageEntity getMessageEntity(Class<?> messageEntityClass) throws BusinessException {
        if (messageEntityClass == null) {
            throw new BusinessException(ErrorCodes.NULL_PARAM)
                    .setMessage("messageEntityClass is null.");
        }

        MessageEntity messageEntity = messageEntityClass.getAnnotation(MessageEntity.class);
        if (messageEntity == null) {
            String msg = String.format("消息实体类上没有 @%s 注解： %s",
                    MessageEntity.class.getSimpleName(), messageEntityClass.getName());
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR)
                    .setMessage(msg);
        }
        return messageEntity;
    }

    private static String getTopicName(MessageEntity messageEntity, Class<?> messageEntityClass) {
        String topicName = messageEntity.topicName();
        if (StringUtils.isBlank(topicName)) {
            topicName = messageEntityClass.getSimpleName() + "Topic";
        }
        return topicName;
    }

    private static String getGroupId(MessageEntity messageEntity, Class<?> messageEntityClass) {
        String groupId = messageEntity.groupId();
        if (StringUtils.isBlank(groupId)) {
            groupId = "GID_" + messageEntityClass.getSimpleName();
//            throw new BusinessException(ErrorCodes.INTERNAL_ERROR)
//                    .setMessage("注册消息消费者时，消息实体类【${messageEntityClass}】的注解上没有指定 groupId.")
//                    .put("messageEntityClass", messageEntityClass.getName());
        }
        return groupId;
    }

    public static final <T> String getTopicName(Class<T> messageEntityClass) throws BusinessException {
        MessageEntity messageEntity = getMessageEntity(messageEntityClass);
        String topicName = getTopicName(messageEntity, messageEntityClass);
        return topicName;
    }

    @Override
    public void send(Object contentObject, String key, String tag) throws BusinessException {
        if (contentObject == null) {
            throw new NullPointerException("message is null.");
        }
        Class<?> clazz = contentObject.getClass();
        MQProducer producer = getOrCreateProducer(clazz);

        String contentText;
        try {
            contentText = Jsons.toJsonText(contentObject);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR, e)
                    .setMessage("Java对象序列化成JSON串出错：${cause}")
                    .put("message", contentObject.toString()).put("cause", e.getMessage());
        }
        if (contentText == null) {
            throw new BusinessException(ErrorCodes.NULL_PARAM)
                    .setMessage("messageText is null.");
        }

        byte[] contentData;
        try {
            contentData = contentText.getBytes("UTF-8"); // 消息内容
        } catch (UnsupportedEncodingException e) {
            // 理论上不会发生这种情况。
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR, e)
                    .setMessage("字符串不是 UTF-8 编码：${cause}")
                    .put("cause", e.getMessage()).put("messageText", contentText);
        }
        if (contentData == null) {
            throw new BusinessException(ErrorCodes.NULL_PARAM)
                    .setMessage("messageContent is null.");
        }

        TopicMessage msg = new TopicMessage(contentData, tag);
        msg.setMessageKey(key);
        producer.publishMessage(msg);
        log.info("publishMessage, key = {}, content = {}", contentText);
    }

    @Override
    public <T> void registConsumer(MessageConsumer<T> consumer,
                                   Class<T> messageEntityClass) throws BusinessException {
        registConsumer(consumer, messageEntityClass, new ConsumerConfig());
    }

    @Override
    public <T> void registConsumer(MessageConsumer<T> consumer, Class<T> messageEntityClass,
                                   ConsumerConfig config) throws BusinessException {
        if (consumer == null) {
            throw new NullPointerException("consumer is null.");
        }
        // 如果不指定，则使用默认配置。
        if (config == null) {
            config = new ConsumerConfig();
        }

        if (consumers.containsKey(consumer)) {
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR)
                    .setMessage("对同一个消费者对象，只能注册一次：${consumer}")
                    .put("consumer", consumer);
        }
        synchronized (consumer) {
            if (consumers.containsKey(consumer)) {
                throw new BusinessException(ErrorCodes.INTERNAL_ERROR)
                        .setMessage("对同一个消费者对象，只能注册一次：${consumer}")
                        .put("consumer", consumer);
            }

            // 创建一个消息中转器，从 MQ 中取消息然后交给业务 consumer 对象来处理。
            MessageEntity messageEntity = getMessageEntity(messageEntityClass);
            String topicName = getTopicName(messageEntity, messageEntityClass);
            String groupId = getGroupId(messageEntity, messageEntityClass);
            MQConsumer mqConsumer = mqClient.getConsumer(instanceId,
                    topicName, groupId, config.getMessageTag());
            MessageConsumerTransfer transfer = new MessageConsumerTransfer(
                    mqConsumer, consumer, messageEntityClass, config);
            transfer.start();
            consumers.put(consumer, transfer);
        }
    }
}
