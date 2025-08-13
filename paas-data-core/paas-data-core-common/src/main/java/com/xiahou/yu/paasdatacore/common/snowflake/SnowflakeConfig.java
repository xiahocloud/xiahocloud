package com.xiahou.yu.paasdatacore.common.snowflake;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法配置类
 *
 * @author paas-data-core-common
 * @version 1.0.0
 */
@Configuration
public class SnowflakeConfig {

    /**
     * 雪花算法配置属性
     */
    public static class SnowflakeProperties {

        /**
         * 工作机器ID (0-31)
         */
        private long workerId = 1L;

        /**
         * 数据中心ID (0-31)
         */
        private long datacenterId = 1L;

        public long getWorkerId() {
            return workerId;
        }

        public void setWorkerId(long workerId) {
            this.workerId = workerId;
        }

        public long getDatacenterId() {
            return datacenterId;
        }

        public void setDatacenterId(long datacenterId) {
            this.datacenterId = datacenterId;
        }
    }

    /**
     * 配置雪花算法ID生成器Bean
     */
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(SnowflakeProperties properties) {
        return new SnowflakeIdGenerator(properties.getWorkerId(), properties.getDatacenterId());
    }

    /**
     * 配置雪花算法属性Bean
     */
    @Bean
    @ConfigurationProperties(prefix = "snowflake")
    public SnowflakeProperties snowflakeProperties() {
        return new SnowflakeProperties();
    }
}
