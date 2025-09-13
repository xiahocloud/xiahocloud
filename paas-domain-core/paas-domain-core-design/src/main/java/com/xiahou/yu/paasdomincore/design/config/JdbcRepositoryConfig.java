package com.xiahou.yu.paasdomincore.design.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.PostgresDialect;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

import java.util.Collections;

/**
 * JDBC Repository 配置
 * 启用Spring Data JDBC Repository扫描
 *
 * @author xiahou
 */
@Configuration
@EnableJdbcRepositories(basePackages = "com.xiahou.yu.paasdomincore.design.repository")
public class JdbcRepositoryConfig extends AbstractJdbcConfiguration {

    /**
     * 定义 Dialect
     */
    @Bean
    @Primary
    public Dialect dialect() {
        return PostgresDialect.INSTANCE;
    }

    /**
     * 自定义命名策略
     * 使用下划线命名策略
     */
    @Bean
    public NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getColumnName(RelationalPersistentProperty property) {
                if (property.isAnnotationPresent(org.springframework.data.relational.core.mapping.Column.class)) {
                    String columnName = property.getRequiredAnnotation(
                            org.springframework.data.relational.core.mapping.Column.class).value();
                    if (!columnName.isEmpty()) {
                        return columnName;
                    }
                }
                return convertCamelCaseToUnderscore(property.getName());
            }

            private String convertCamelCaseToUnderscore(String name) {
                return name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            }
        };
    }

    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Collections.emptyList());
    }

}
