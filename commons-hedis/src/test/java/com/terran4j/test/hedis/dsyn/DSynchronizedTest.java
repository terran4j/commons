package com.terran4j.test.hedis.dsyn;

import com.terran4j.commons.hedis.dsyn.DSynchArgs;
import com.terran4j.commons.hedis.dsyn.DSynchronizedAspect;
import com.terran4j.commons.util.error.BusinessException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DSynchronizedTest extends BaseCacheTest {

    private static final Logger log = LoggerFactory.getLogger(DSynchronizedTest.class);

    private final String key = "k1";

    @Before
    public void setUp() {
        super.setUp();
        cacheService.remove(key);
        cacheService.remove("increment-k1");
    }

    public String toKey() {
        int a = (int) DSynchArgs.get("a");
        int b = (int) DSynchArgs.get("b");
        if (a < b) {
            return a + "-" + b;
        } else {
            return b + "-" + a;
        }
    }

    public String toKey(int a, int b) {
        if (a < b) {
            return a + "-" + b;
        } else {
            return b + "-" + a;
        }
    }

    public void doSomething(@Param("a") int a, @Param("b") int b) {
    }

    @Test
    public void testGetLockKeyWithArgs() {
        Method method = ReflectionUtils.findMethod(getClass(), "doSomething",
                int.class, int.class);
        Assert.assertNotNull(method);
        String keyEL = "#target.toKey(#a, #b)";
        String key = null;
        try {
            key = DSynchronizedAspect.getLockKey(keyEL, this, method, new Object[]{1, 6});
            Assert.assertEquals("1-6", key);
            key = DSynchronizedAspect.getLockKey(keyEL, this, method, new Object[]{3, 2});
            Assert.assertEquals("2-3", key);
        } catch (BusinessException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetLockKey() {
        Method method = ReflectionUtils.findMethod(getClass(), "doSomething",
                int.class, int.class);
        Assert.assertNotNull(method);
        String keyEL = "#target.toKey()";
        String key = null;
        try {
            key = DSynchronizedAspect.getLockKey(keyEL, this, method, new Object[]{1, 9});
            Assert.assertEquals("1-9", key);
            key = DSynchronizedAspect.getLockKey(keyEL, this, method, new Object[]{3, 2});
            Assert.assertEquals("2-3", key);
        } catch (BusinessException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDSynchronized() throws Exception {
        int result = dSynchronizedService.incrementAndGet(key, 0, 0);
        Assert.assertEquals(1, result);
        dSynchronizedService.incrementAndGet(key, 0, 0);
        result = dSynchronizedService.getValue(key);
        Assert.assertEquals(2, result);
    }

    @Test
    public void testDSynchronizedMultiThread() throws Exception {
        final int threadCount = 5;
        final int exeCount = 10;
        final List<Throwable> failed = new ArrayList<>();
        Thread[] threads = new Thread[threadCount];
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < exeCount; j++) {
                    try {
                        dSynchronizedService.incrementAndGet(
                                key, -1, -1);
                    } catch (Throwable e) {
                        failed.add(e);
                    }
                }
            });
            threads[i].setName("Thread - " + i);
            threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            threads[i].join();
        }
        long spend = System.currentTimeMillis() - t0;
        if (log.isInfoEnabled()) {
            log.info("{} threads, execute {} times per thread, total spend {}ms",
                    threadCount, exeCount, spend);
        }
        for (Throwable fail : failed) {
            fail.printStackTrace();
        }
        Assert.assertFalse(failed.size() > 0);
        Assert.assertEquals(threadCount * exeCount,
                dSynchronizedService.getValue(key));
    }
}
