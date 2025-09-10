package com.xiahou.yu.paasdomincore.design.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jdbc.core.convert.BasicJdbcConverter;
import org.springframework.data.jdbc.core.convert.DefaultJdbcTypeFactory;
import org.springframework.data.jdbc.core.convert.JdbcArrayColumns; // 确保导入
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.PostgresDialect;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;

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

    /**
     * 定义 Dialect
     */
    @Bean
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

    @Bean
    public JdbcMappingContext jdbcMappingContext(NamingStrategy namingStrategy,
                                                 JdbcCustomConversions customConversions) {
        JdbcMappingContext mappingContext = new JdbcMappingContext(namingStrategy);
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        return mappingContext;
    }


    /**

    /**
     * 创建 JdbcArrayColumns Bean
     * 使用 Dialect 提供的数组支持
     */
    @Bean
    public JdbcArrayColumns jdbcArrayColumns(Dialect dialect) {
        // 从 Dialect 获取数组支持
        return dialect.getArraySupport();
    }

    /**
     * 修正后的 JdbcConverter 配置
     */
    @Bean
    public JdbcConverter jdbcConverter(JdbcMappingContext mappingContext,
                                       @Lazy RelationResolver relationResolver,
                                       JdbcCustomConversions customConversions,
                                       NamedParameterJdbcOperations operations,
                                       JdbcArrayColumns jdbcArrayColumns) {

        // 使用正确的构造函数
        DefaultJdbcTypeFactory typeFactory = new DefaultJdbcTypeFactory(
                operations.getJdbcOperations(),
                jdbcArrayColumns
        );

        return new BasicJdbcConverter(mappingContext, relationResolver, customConversions, typeFactory);
    }
}

