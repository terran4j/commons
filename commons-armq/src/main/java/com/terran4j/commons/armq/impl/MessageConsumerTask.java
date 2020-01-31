package com.terran4j.commons.armq.impl;

import com.terran4j.commons.util.task.LoopExecuteTask;

public class MessageConsumerTask extends LoopExecuteTask {

    @Override
    protected boolean execute() throws Exception {
        return true;
    }
}
