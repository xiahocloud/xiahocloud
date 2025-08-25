package com.xiahou.yu.paasdomincore.runtime.config;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.executor.DataOperationExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 数据操作处理器自动配置
 * 自动注册所有的处理器到执行器中
 *
 * @author xiahou
 */
@Configuration
@DependsOn("defaultDataOperationExecutor")
@RequiredArgsConstructor
@Slf4j
public class DataOperationHandlerAutoConfiguration {


    private final DataOperationExecutor dataOperationExecutor;

    private final List<Handler> handlers;

    @PostConstruct
    public void registerHandlers() {
        log.info("Auto-registering {} handlers", handlers.size());

        for (Handler handler : handlers) {
            // 根据处理器的特性决定注册为前置还是后置处理器
            if (isPreHandler(handler)) {
                dataOperationExecutor.registerPreHandler(handler.getName(), handler);
                log.info("Registered pre-handler: {} with order: {}", handler.getName(), handler.getOrder());
            } else {
                dataOperationExecutor.registerPostHandler(handler.getName(), handler);
                log.info("Registered post-handler: {} with order: {}", handler.getName(), handler.getOrder());
            }
        }

        log.info("All handlers registered successfully");
    }

    private boolean isPreHandler(Handler handler) {
        // 根据处理器名称或类型判断是前置还是后置处理器
        String handlerName = handler.getName();

        // 以下处理器作为前置处理器
        return handlerName.contains("Validation") ||
               handlerName.contains("Permission") ||
               handlerName.contains("AutoFill") ||
               handlerName.contains("Audit");
    }
}
