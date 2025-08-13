package com.xiahou.yu.paasdatacore.design.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * 审计配置
 * 启用Spring Data JDBC的审计功能
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Configuration
@EnableJdbcAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AuditConfig {

    /**
     * 审计时间提供器
     * 使用OffsetDateTime以支持时区
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
}
