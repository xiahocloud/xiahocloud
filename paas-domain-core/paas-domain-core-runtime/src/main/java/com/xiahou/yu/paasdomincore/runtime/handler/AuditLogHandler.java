package com.xiahou.yu.paasdomincore.runtime.handler;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审计日志处理器
 * 在数据操作前后记录审计信息
 *
 * @author xiahou
 */
@Component
@Slf4j
public class AuditLogHandler implements Handler {

    @Override
    public boolean handle(CommandContext context, HandlerChain chain) {
        String operation = context.getAttribute("commandType");

        // 记录操作开始
        logOperationStart(context, operation);

        // 继续执行链条
        boolean result = chain.proceed(context);

        // 记录操作结果
        logOperationEnd(context, operation, result);

        return result;
    }

    @Override
    public String getName() {
        return "AuditLogHandler";
    }

    @Override
    public int getOrder() {
        return 200; // 中等优先级
    }

    @Override
    public boolean supports(CommandContext context) {
        // 支持所有操作
        return true;
    }

    private void logOperationStart(CommandContext context, String operation) {
        String aggr = context.getAttribute("aggr");
        log.info("AUDIT_START: Operation={}, Entity={}.{}, User={}, Time={}",
                operation,
                aggr,
                context.getEntityName(),
                context.getAttribute("userId"),
                LocalDateTime.now());
    }

    private void logOperationEnd(CommandContext context, String operation, boolean success) {
        String aggr = context.getAttribute("aggr");
        log.info("AUDIT_END: Operation={}, Entity={}.{}, Success={}, Time={}",
                operation,
                aggr,
                context.getEntityName(),
                success,
                LocalDateTime.now());
    }
}
