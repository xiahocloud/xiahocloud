package com.xiahou.yu.paasdomincore.design.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JDBC仓储配置
 * 启用Spring Data JDBC仓储和事务管理
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Configuration
@EnableJdbcRepositories(basePackages = "com.xiahou.yu.paasdomincore.design.repository")
@EnableTransactionManagement
public class JdbcRepositoryConfig {

    // Spring Data JDBC会自动配置所需的组件
    // 如果需要自定义配置，可以在这里添加Bean定义
}
