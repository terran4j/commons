package com.terran4j.commons.armq;

import com.terran4j.commons.util.error.BusinessException;

public interface MessageConsumer<T> {

    /**
     * @param key
     * @param content
     * @throws BusinessException
     * @warn 请务必确保消息消费的幂等性（极端情况下存在已消费的消息重复消费的情况）。
     */
    void onMessage(String key, T content) throws BusinessException;
}
