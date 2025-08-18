package com.xiahou.yu.paaswebserver.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.scalars.ExtendedScalars;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * GraphQL配置类
 *
 * @author xiahou
 */
@Configuration
@Slf4j
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                // 注册自定义标量类型
                .scalar(ExtendedScalars.Json)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.GraphQLLong)
                // 设置异常处理器
                .dataFetcherExceptionHandler(new CustomDataFetcherExceptionHandler());
    }

    /**
     * 自定义异常处理器
     */
    @Slf4j
    public static class CustomDataFetcherExceptionHandler implements DataFetcherExceptionHandler {

        @Override
        public DataFetcherExceptionHandlerResult handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
            Throwable exception = handlerParameters.getException();
            DataFetchingEnvironment environment = handlerParameters.getDataFetchingEnvironment();

            log.error("GraphQL execution error at path: {}", environment.getExecutionStepInfo().getPath(), exception);

            GraphQLError error = GraphqlErrorBuilder.newError()
                    .message(exception.getMessage())
                    .path(environment.getExecutionStepInfo().getPath())
                    .location(environment.getField().getSourceLocation())
                    .build();

            return DataFetcherExceptionHandlerResult.newResult()
                    .error(error)
                    .build();
        }
    }
}
