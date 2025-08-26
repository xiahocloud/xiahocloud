package com.xiahou.yu.paasdomincore.runtime.executor;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.Command;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import com.xiahou.yu.paasdomincore.design.executor.DataOperationExecutor;
import com.xiahou.yu.paasdomincore.runtime.chain.DefaultHandlerChain;
import lombok.extern.slf4j.Slf4j;
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
@Component
@Slf4j
public class DefaultDataOperationExecutor implements DataOperationExecutor {

    private final Map<String, Handler> preHandlers = new ConcurrentHashMap<>();
    private final Map<String, Handler> postHandlers = new ConcurrentHashMap<>();
    private final Map<CommandType, DataOperationStrategy> strategies = new ConcurrentHashMap<>();

    public DefaultDataOperationExecutor() {
        initializeStrategies();
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
                commandType, aggr, context.getEntity());

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
            preHandlers.put(handlerName, (Handler) handler);
            log.info("Registered pre-handler: {}", handlerName);
        } else {
            throw new IllegalArgumentException("Handler must implement Handler interface");
        }
    }

    @Override
    public void registerPostHandler(String handlerName, Object handler) {
        if (handler instanceof Handler) {
            postHandlers.put(handlerName, (Handler) handler);
            log.info("Registered post-handler: {}", handlerName);
        } else {
            throw new IllegalArgumentException("Handler must implement Handler interface");
        }
    }

    @Override
    public void removeHandler(String handlerName) {
        preHandlers.remove(handlerName);
        postHandlers.remove(handlerName);
        log.info("Removed handler: {}", handlerName);
    }

    private HandlerChain createPreHandlerChain(CommandContext context) {
        List<Handler> handlers = new ArrayList<>(preHandlers.values());
        return new DefaultHandlerChain(handlers);
    }

    private HandlerChain createPostHandlerChain(CommandContext context) {
        List<Handler> handlers = new ArrayList<>(postHandlers.values());
        return new DefaultHandlerChain(handlers);
    }

    private void initializeStrategies() {
        strategies.put(CommandType.CREATE, new CreateOperationStrategy());
        strategies.put(CommandType.UPDATE, new UpdateOperationStrategy());
        strategies.put(CommandType.DELETE, new DeleteOperationStrategy());
        strategies.put(CommandType.QUERY, new QueryOperationStrategy());
    }

    /**
     * 数据操作策略接口
     */
    private interface DataOperationStrategy {
        Object execute(CommandContext context);
    }

    private interface EntityExecutor {
        Object metaEntityHandler(CommandContext context);

        Object stdEntityHandler(CommandContext context);

        Object customEntityHandler(CommandContext context);
    }

    /**
     * 创建操作策略
     */
    private static class CreateOperationStrategy implements DataOperationStrategy, EntityExecutor {
        @Override
        public Object execute(CommandContext context) {
            log.info("Executing CREATE operation for {}", context.getEntity());
            // 这里应该调用实际的数据访问层进行数据创建
            return Map.of("success", true, "message", "Data created successfully");
        }

        @Override
        public Object metaEntityexecute(CommandContext context) {

        }

        @Override
        public Object stdEntityexecute(CommandContext context) {

        }

        @Override
        public Object customEntityHandler(CommandContext context) {

        }

    }

    /**
     * 更新操作策略
     */
    private static class UpdateOperationStrategy implements DataOperationStrategy, EntityExecutor {
        @Override
        public Object execute(CommandContext context) {
            String aggr = context.getAttribute("aggr");
            log.info("Executing UPDATE operation for {}.{}", aggr, context.getEntity());
            // 这里应该调用实际的数据访问层进行数据更新
            return Map.of("success", true, "message", "Data updated successfully");
        }
    }

    /**
     * 删除操作策略
     */
    private static class DeleteOperationStrategy implements DataOperationStrategy, EntityExecutor {
        @Override
        public Object execute(CommandContext context) {
            String aggr = context.getAttribute("aggr");
            log.info("Executing DELETE operation for {}.{}", aggr, context.getEntity());
            // 这里应该调用实际的数据访问层进行数据删除
            return Map.of("success", true, "message", "Data deleted successfully");
        }
    }

    /**
     * 查询操作策略
     */
    private static class QueryOperationStrategy implements DataOperationStrategy, EntityExecutor {
        @Override
        public Object execute(CommandContext context) {
            String aggr = context.getAttribute("aggr");
            log.info("Executing QUERY operation for {}.{}", aggr, context.getEntity());
            // 这里应该调用实际的数据访问层进行数据查询
            return Map.of("success", true, "data", List.of(), "message", "Data queried successfully");
        }
    }
}
