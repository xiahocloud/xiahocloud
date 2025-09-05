package com.xiahou.yu.paasdomincore.runtime.executor;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.Command;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import com.xiahou.yu.paasdomincore.design.executor.DataOperationExecutor;
import com.xiahou.yu.paasdomincore.runtime.chain.DefaultHandlerChain;
import com.xiahou.yu.paasdomincore.runtime.strategy.DataOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.impl.CreateOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.impl.DeleteOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.impl.QueryOperationStrategy;
import com.xiahou.yu.paasdomincore.runtime.strategy.impl.UpdateOperationStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据操作执行器默认实现
 * 微内核架构的核心实现，支持插件化的数据操作处理
 *
 * @author xiahou
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultDataOperationExecutor implements DataOperationExecutor {

    private static final Map<String, Handler> PRE_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, Handler> POST_HANDLERS = new ConcurrentHashMap<>();
    private final Map<CommandType, DataOperationStrategy> strategies = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;
    private final CreateOperationStrategy createOperationStrategy;
    private final UpdateOperationStrategy updateOperationStrategy;
    private final DeleteOperationStrategy deleteOperationStrategy;
    private final QueryOperationStrategy queryOperationStrategy;

    @PostConstruct
    private void initializeStrategies() {
        strategies.put(CommandType.CREATE, createOperationStrategy);
        strategies.put(CommandType.UPDATE, updateOperationStrategy);
        strategies.put(CommandType.DELETE, deleteOperationStrategy);
        strategies.put(CommandType.QUERY, queryOperationStrategy);
        log.info("Initialized {} operation strategies", strategies.size());
    }

    @Override
    public <T> T execute(Command<T> command) {
        return execute(command.getContext(), command.getCommandType());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(CommandContext context, CommandType commandType) {
        String aggr = context.getAttribute("aggr");
        log.info("Executing {} operation for entity: {}.{}",
                commandType, aggr, context.getEntityName());

        try {
            // 1. 执行前置处理器链
            HandlerChain preChain = createPreHandlerChain(context);
            boolean preResult = preChain.proceed(context);
            if (!preResult) {
                log.warn("Pre-processing failed, operation aborted");
                return null;
            }

            // 2. 执行核心业务逻辑
            DataOperationStrategy strategy = strategies.get(commandType);
            if (strategy == null) {
                throw new UnsupportedOperationException("Unsupported command type: " + commandType);
            }

            T result = (T) strategy.execute(context);

            // 3. 将结果放入上下文以便后置处理器使用
            context.setAttribute("result", result);

            // 4. 执行后置处理器链
            HandlerChain postChain = createPostHandlerChain(context);
            boolean postResult = postChain.proceed(context);
            if (!postResult) {
                log.warn("Post-processing failed, but operation completed");
            }

            return result;
        } catch (Exception e) {
            log.error("Error executing {} operation", commandType, e);
            throw new RuntimeException("Operation execution failed", e);
        }
    }

    @Override
    public void registerPreHandler(String handlerName, Object handler) {
        if (handler instanceof Handler) {
            PRE_HANDLERS.put(handlerName, (Handler) handler);
            log.info("Registered pre-handler: {}", handlerName);
        } else {
            throw new IllegalArgumentException("Handler must implement Handler interface");
        }
    }

    @Override
    public void registerPostHandler(String handlerName, Object handler) {
        if (handler instanceof Handler) {
            POST_HANDLERS.put(handlerName, (Handler) handler);
            log.info("Registered post-handler: {}", handlerName);
        } else {
            throw new IllegalArgumentException("Handler must implement Handler interface");
        }
    }

    @Override
    public void removeHandler(String handlerName) {
        PRE_HANDLERS.remove(handlerName);
        POST_HANDLERS.remove(handlerName);
        log.info("Removed handler: {}", handlerName);
    }

    private HandlerChain createPreHandlerChain(CommandContext context) {
        List<Handler> handlers = new ArrayList<>(PRE_HANDLERS.values());
        return new DefaultHandlerChain(handlers);
    }

    private HandlerChain createPostHandlerChain(CommandContext context) {
        List<Handler> handlers = new ArrayList<>(POST_HANDLERS.values());
        return new DefaultHandlerChain(handlers);
    }
}
