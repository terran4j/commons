package com.terran4j.commons.armq;

import com.aliyun.mq.http.MQClient;
import com.terran4j.commons.armq.impl.MessageServiceImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ArmqConfig {

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.rocketMQ.instanceId}")
    private String instanceId;

    @Value("${aliyun.rocketMQ.endpoint}")
    private String endpoint;

    @Bean
    public MQClient mqClient() {
        MQClient mqClient = new MQClient(
                endpoint, // 设置HTTP接入域名（此处以公共云生产环境为例）
                accessKeyId, // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
                accessKeySecret // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        );
        return mqClient;
    }

    @Bean
    public MessageService armqFactory(MQClient mqClient) {
        return new MessageServiceImpl(instanceId, mqClient);
    }

}
