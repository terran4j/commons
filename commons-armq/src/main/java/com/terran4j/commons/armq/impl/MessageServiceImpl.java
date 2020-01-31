package com.terran4j.commons.armq.impl;

import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQConsumer;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.terran4j.commons.armq.ConsumerConfig;
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

    private final Map<Class<?>, MQConsumer> consumers = new ConcurrentHashMap<>();

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

    private MessageEntity getMessageEntity(Class<?> messageEntityClass) throws BusinessException {
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

    private String getTopicName(MessageEntity messageEntity, Class<?> messageEntityClass) {
        String topicName = messageEntity.topicName();
        if (StringUtils.isBlank(topicName)) {
            topicName = messageEntityClass.getSimpleName();
        }
        return topicName;
    }

    private String getGroupId(MessageEntity messageEntity, Class<?> messageEntityClass) throws BusinessException {
        String topicName = messageEntity.groupId();
        if (StringUtils.isBlank(topicName)) {
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR)
                    .setMessage("注册消息消费者时，消息实体类【${messageEntityClass}】的注解上没有指定 groupId.")
                    .put("messageEntityClass", messageEntityClass.getName());
        }
        return topicName;
    }

    @Override
    public void send(Object content, String key, String tag) throws BusinessException {
        if (content == null) {
            throw new NullPointerException("message is null.");
        }
        Class<?> clazz = content.getClass();
        MQProducer producer = getOrCreateProducer(clazz);

        String messageText;
        try {
            messageText = Jsons.toJsonText(content);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR, e)
                    .setMessage("Java对象序列化成JSON串出错：${cause}")
                    .put("message", content.toString()).put("cause", e.getMessage());
        }
        if (messageText == null) {
            throw new BusinessException(ErrorCodes.NULL_PARAM)
                    .setMessage("messageText is null.");
        }

        byte[] messageContent;
        try {
            messageContent = messageText.getBytes("UTF-8"); // 消息内容
        } catch (UnsupportedEncodingException e) {
            // 理论上不会发生这种情况。
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR, e)
                    .setMessage("字符串不是 UTF-8 编码：${cause}")
                    .put("cause", e.getMessage()).put("messageText", messageText);
        }
        if (messageContent == null) {
            throw new BusinessException(ErrorCodes.NULL_PARAM)
                    .setMessage("messageContent is null.");
        }

        TopicMessage msg = new TopicMessage(messageContent, tag);
        msg = producer.publishMessage(msg);
        log.info("publishMessage: {}", msg);
    }

    @Override
    public <T> void registConsumer(Class<T> messageEntityClass) {

    }

    @Override
    public <T> void registConsumer(Class<T> messageEntityClass, ConsumerConfig config) throws BusinessException {
        MessageEntity messageEntity = getMessageEntity(messageEntityClass);
        String topicName = getTopicName(messageEntity, messageEntityClass);
        String groupId = messageEntity.groupId();

        MQConsumer consumer = mqClient.getConsumer(instanceId, topicName, groupId, config.getMessageTag());


        ;
    }
}
