package com.xiahou.yu.paasdatacore.common.snowflake;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 雪花算法配置测试类
 */
@SpringBootTest(classes = SnowflakeConfig.class)
@TestPropertySource(properties = {
    "snowflake.worker-id=5",
    "snowflake.datacenter-id=3"
})
class SnowflakeConfigTest {

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private SnowflakeConfig.SnowflakeProperties snowflakeProperties;

    @Test
    void testSnowflakeIdGeneratorBean() {
        assertNotNull(snowflakeIdGenerator);

        // 测试ID生成
        long id1 = snowflakeIdGenerator.nextId();
        long id2 = snowflakeIdGenerator.nextId();

        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
    }

    @Test
    void testSnowflakeProperties() {
        assertNotNull(snowflakeProperties);
        assertEquals(5L, snowflakeProperties.getWorkerId());
        assertEquals(3L, snowflakeProperties.getDatacenterId());
    }

    @Test
    void testConfiguredIdGenerator() {
        // 测试配置的生成器是否使用了正确的参数
        long id = snowflakeIdGenerator.nextId();
        SnowflakeIdGenerator.SnowflakeIdInfo info = snowflakeIdGenerator.parseId(id);

        assertEquals(3L, info.getDatacenterId());
        assertEquals(5L, info.getWorkerId());
    }
}
