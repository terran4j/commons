package com.terran4j.commons.armq;

import com.terran4j.commons.util.error.BusinessException;

public interface MessageConsumer<T> {

    void onMessage(T message) throws BusinessException;
}
