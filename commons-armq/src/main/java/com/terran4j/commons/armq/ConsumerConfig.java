package com.terran4j.commons.armq;

import lombok.Data;

@Data
public class ConsumerConfig {

    private int batchSize = 1;

    private int pollingSecond = 5;

    private int threadSize = 1;

    private long threadSleepTime = 0;

    /**
     * 用于消息过滤的 tag 表达式，具体语法请参见阿里云的文档：<br>
     * https://help.aliyun.com/document_detail/29543.html?spm=a2c4g.11186623.2.16.234e38cbxARe8c#concept-2047069
     */
    private String messageTag = null;

}
