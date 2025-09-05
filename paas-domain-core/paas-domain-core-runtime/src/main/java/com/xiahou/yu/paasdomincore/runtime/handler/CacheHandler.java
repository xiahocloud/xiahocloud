package com.xiahou.yu.paasdomincore.runtime.handler;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存处理器
 * 在查询操作时优先从缓存获取数据，在增删改操作后更新缓存
 *
 * @author xiahou
 */
@Component
@Slf4j
public class CacheHandler implements Handler {

    // 简单的内存缓存实现，实际应用中应该使用Redis等
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public boolean handle(CommandContext context, HandlerChain chain) {
        String operation = context.getAttribute("commandType");
        String cacheKey = buildCacheKey(context);

        if ("QUERY".equals(operation)) {
            // 查询操作：先检查缓存
            return handleQuery(context, chain, cacheKey);
        } else {
            // 增删改操作：先执行操作，再更新缓存
            return handleModification(context, chain, cacheKey, operation);
        }
    }

    @Override
    public String getName() {
        return "CacheHandler";
    }

    @Override
    public int getOrder() {
        return 300; // 较低优先级，在其他处理器之后执行
    }

    @Override
    public boolean supports(CommandContext context) {
        // 支持所有操作
        return true;
    }

    private boolean handleQuery(CommandContext context, HandlerChain chain, String cacheKey) {
        // 先尝试从缓存获取
        Object cachedResult = cache.get(cacheKey);
        if (cachedResult != null) {
            log.info("Cache hit for key: {}", cacheKey);
            context.setAttribute("result", cachedResult);
            context.setAttribute("fromCache", true);
            return true; // 直接返回，不继续执行链条
        }

        log.info("Cache miss for key: {}", cacheKey);

        // 继续执行链条获取数据
        boolean result = chain.proceed(context);

        if (result) {
            // 将结果放入缓存
            Object queryResult = context.getAttribute("result");
            if (queryResult != null) {
                cache.put(cacheKey, queryResult);
                log.info("Cached result for key: {}", cacheKey);
            }
        }

        return result;
    }

    private boolean handleModification(CommandContext context, HandlerChain chain, String cacheKey, String operation) {
        // 先执行操作
        boolean result = chain.proceed(context);

        if (result) {
            // 操作成功后更新缓存
            if ("DELETE".equals(operation)) {
                cache.remove(cacheKey);
                log.info("Removed cache for key: {}", cacheKey);
            } else {
                // CREATE 或 UPDATE 操作，清除相关缓存
                clearRelatedCache(context);
                log.info("Cleared related cache for operation: {}", operation);
            }
        }

        return result;
    }

    private String buildCacheKey(CommandContext context) {
        StringBuilder keyBuilder = new StringBuilder();
        String system = context.getAttribute("system");
        String module = context.getAttribute("module");
        String aggr = context.getAttribute("aggr");

        keyBuilder.append(system)
                  .append(":")
                  .append(module)
                  .append(":")
                  .append(aggr)
                  .append(":")
                  .append(context.getEntityName());

        // 对于查询操作，添加查询条件到缓存键
        if (context.getConditions() != null && !context.getConditions().isEmpty()) {
            keyBuilder.append(":").append(context.getConditions().hashCode());
        }

        return keyBuilder.toString();
    }

    private void clearRelatedCache(CommandContext context) {
        String system = context.getAttribute("system");
        String module = context.getAttribute("module");
        String aggr = context.getAttribute("aggr");

        String prefix = String.format("%s:%s:%s:%s",
                system, module, aggr, context.getEntityName());

        // 清除所有相关的缓存项
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(prefix));
    }
}
