package com.xiahou.yu.paasdatacore.design.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

import java.util.Collections;

/**
 * JDBC配置
 * 自定义Spring Data JDBC的映射和转换配置
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Configuration
public class JdbcConfig {

    /**
     * 自定义命名策略
     * 使用下划线命名策略
     */
    @Bean
    public NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getColumnName(RelationalPersistentProperty property) {
                // 如果属性有@Column注解，使用注解中的值
                if (property.isAnnotationPresent(org.springframework.data.relational.core.mapping.Column.class)) {
                    String columnName = property.getRequiredAnnotation(
                        org.springframework.data.relational.core.mapping.Column.class).value();
                    if (!columnName.isEmpty()) {
                        return columnName;
                    }
                }
                // 否则使用默认的下划线命名策略
                return convertCamelCaseToUnderscore(property.getName());
            }

            private String convertCamelCaseToUnderscore(String name) {
                return name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
            }
        };
    }

    /**
     * 自定义类型转换
     */
    @Bean
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Collections.emptyList());
    }

    /**
     * JDBC映射上下文
     */
    @Bean
    public JdbcMappingContext jdbcMappingContext(NamingStrategy namingStrategy,
                                                 JdbcCustomConversions customConversions) {
        JdbcMappingContext mappingContext = new JdbcMappingContext(namingStrategy);
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        return mappingContext;
    }
}
