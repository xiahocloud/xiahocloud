package com.xiahou.yu.paasdomincore.design.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * JDBC Repository 配置
 * 启用Spring Data JDBC Repository扫描
 *
 * @author xiahou
 */
@Configuration
@EnableJdbcRepositories(basePackages = "com.xiahou.yu")
public class JdbcRepositoryConfig {
}
