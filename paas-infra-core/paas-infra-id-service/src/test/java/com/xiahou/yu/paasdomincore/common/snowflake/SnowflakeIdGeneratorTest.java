package com.xiahou.yu.paasdomincore.common.snowflake;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 雪花算法ID生成器测试类
 */
class SnowflakeIdGeneratorTest {

    private SnowflakeIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new SnowflakeIdGenerator(1L, 1L);
    }

    @Test
    void testGenerateId() {
        // 生成ID应该为正数
        long id = generator.nextId();
        assertTrue(id > 0);
    }

    @Test
    void testGenerateUniqueIds() {
        Set<Long> ids = new HashSet<>();
        int count = 10000;

        // 生成10000个ID，确保唯一性
        for (int i = 0; i < count; i++) {
            long id = generator.nextId();
            assertTrue(ids.add(id), "Generated duplicate ID: " + id);
        }

        assertEquals(count, ids.size());
    }

    @Test
    void testParseId() {
        long id = generator.nextId();
        SnowflakeIdGenerator.SnowflakeIdInfo info = generator.parseId(id);

        assertNotNull(info);
        assertEquals(1L, info.getDatacenterId());
        assertEquals(1L, info.getWorkerId());
        assertTrue(info.getTimestamp() > 0);
        assertTrue(info.getSequence() >= 0);
    }

    @Test
    void testConcurrentGeneration() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Long> allIds = new HashSet<>();
        AtomicInteger duplicateCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Set<Long> threadIds = new HashSet<>();
                    for (int j = 0; j < idsPerThread; j++) {
                        long id = generator.nextId();
                        if (!threadIds.add(id)) {
                            duplicateCount.incrementAndGet();
                        }
                    }
                    synchronized (allIds) {
                        for (Long id : threadIds) {
                            if (!allIds.add(id)) {
                                duplicateCount.incrementAndGet();
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(0, duplicateCount.get(), "Found duplicate IDs in concurrent generation");
        assertEquals(threadCount * idsPerThread, allIds.size());
    }

    @Test
    void testInvalidWorkerIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(32L, 1L));
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(-1L, 1L));
    }

    @Test
    void testInvalidDatacenterIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(1L, 32L));
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(1L, -1L));
    }

    @Test
    void testSequentialIds() {
        // 测试连续生成的ID是递增的
        long previousId = generator.nextId();
        for (int i = 0; i < 100; i++) {
            long currentId = generator.nextId();
            assertTrue(currentId > previousId, "IDs should be increasing");
            previousId = currentId;
        }
    }

    @Test
    void testSnowflakeIdInfoToString() {
        long id = generator.nextId();
        SnowflakeIdGenerator.SnowflakeIdInfo info = generator.parseId(id);
        String infoString = info.toString();

        assertNotNull(infoString);
        assertTrue(infoString.contains("SnowflakeIdInfo"));
        assertTrue(infoString.contains("timestamp="));
        assertTrue(infoString.contains("datacenterId="));
        assertTrue(infoString.contains("workerId="));
        assertTrue(infoString.contains("sequence="));
    }
}
