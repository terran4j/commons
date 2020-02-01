package com.terran4j.commons.test.armq;

import com.terran4j.commons.armq.ArmqConfig;
import com.terran4j.commons.armq.MessageConsumer;
import com.terran4j.commons.armq.MessageService;
import com.terran4j.commons.util.error.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootTest(classes = {ArmqConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NormalTopicTest implements MessageConsumer<Normal> {

    @Autowired
    private MessageService messageService;

    private Map<String, Normal> consumedMessages = new ConcurrentHashMap<>();

    private CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void init() throws BusinessException {
        consumedMessages.clear();
        latch = new CountDownLatch(1);
        messageService.registConsumer(this, Normal.class);
    }

    @Override
    public void onMessage(String key, Normal content) throws BusinessException {
        consumedMessages.put(key, content);
        latch.countDown();
    }

    @Test
    public void testNormalTopic() throws Exception {
        log.info("testNormalTopic");

        String key = "normal-5";
        Normal msg = new Normal();
        msg.setType(5L);
        msg.setMsg("normal msg!");
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record1 = new HashMap<>();
        record1.put("id", "1");
        records.add(record1);
        Map<String, String> record2 = new HashMap<>();
        record2.put("id", "2");
        records.add(record2);
        msg.setRecords(records);

        long t0 = System.currentTimeMillis();
        messageService.send(msg, key, null);
        latch.await();
        Normal consumedMsg = consumedMessages.get(key);
        Assert.assertEquals(msg, consumedMsg);
        long t1 = System.currentTimeMillis();
        log.info("send and receive msg ,spend: {}", (t1 - t0));
    }

}
