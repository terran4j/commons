package com.terran4j.commons.armq;

import lombok.Data;

@Data
public class ConsumerConfig {

    /**
     * 每次拉取多少条消息。
     */
    private int batchSize = 1;

    /**
     * 从 RocketMQ 请求拉取消息时，若没有消息，请求会 hold 住多长时间才返回。
     */
    private int pollingSecond = 5;

    /**
     * 起多少个守护线程，用于不停的拉取消息。
     */
    private int threadSize = 1;

    /**
     * 当拉取不到消息、拉取消息出错、或消费消息出错等异常情况发现时，
     * 线程睡眠多长时间再试（单位为毫秒）
     */
    private long threadSleepTime = 1000;

    /**
     * 用于消息过滤的 tag 表达式，具体语法请参见阿里云的文档：<br>
     * https://help.aliyun.com/document_detail/29543.html?spm=a2c4g.11186623.2.16.234e38cbxARe8c#concept-2047069
     */
    private String messageTag = null;

}
