package com.xiahou.yu.paaswebserver.config;

import com.xiahou.yu.paaswebserver.dynamic.DynamicGraphQLSchemaBuilder;
import graphql.schema.GraphQLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.execution.GraphQlSource;

@Configuration
public class DynamicGraphQLConfig {

    @Autowired
    private DynamicGraphQLSchemaBuilder schemaBuilder;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "graphql.dynamic.enabled", havingValue = "true", matchIfMissing = true)
    public GraphQlSource dynamicGraphQlSource() {
        try {
            GraphQLSchema schema = schemaBuilder.buildSchema();
            return GraphQlSource.builder()
                    .schema(schema)
                    .build();
        } catch (Exception e) {
            // 如果动态schema构建失败，回退到默认配置
            System.err.println("Failed to build dynamic GraphQL schema: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
