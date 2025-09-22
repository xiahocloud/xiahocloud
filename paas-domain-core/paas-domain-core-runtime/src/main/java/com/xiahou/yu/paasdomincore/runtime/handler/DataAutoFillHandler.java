package com.xiahou.yu.paasdomincore.runtime.handler;

import com.xiahou.yu.paasdomincore.common.snowflake.SnowflakeIdGenerator;
import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据自动填充处理器
 * 在数据操作前自动填充系统字段
 *
 * @author xiahou
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataAutoFillHandler implements Handler {

    private final SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public boolean handle(CommandContext context, HandlerChain chain) {
        String aggr = context.getAttribute("aggr");
        log.info("Auto-filling system fields for {}.{}", aggr, context.getEntityName());

        String operation = context.getAttribute("commandType");

        if ("CREATE".equals(operation)) {
            fillCreateFields(context);
        } else if ("UPDATE".equals(operation)) {
            fillUpdateFields(context);
        }

        return chain.proceed(context);
    }

    @Override
    public String getName() {
        return "DataAutoFillHandler";
    }

    @Override
    public int getOrder() {
        return 150; // 在验证之后，操作之前执行
    }

    @Override
    public boolean supports(CommandContext context) {
        String operation = context.getAttribute("commandType");
        return "CREATE".equals(operation) || "UPDATE".equals(operation);
    }

    private void fillCreateFields(CommandContext context) {
        Map<String, Object> data = context.getData();
        if (data == null) {
            data = new HashMap<>();
            context.setData(data);
        }

        LocalDateTime now = LocalDateTime.now();
        String currentUser = context.getRequestContext().getUserId();

        long nextId = snowflakeIdGenerator.nextId();
        data.put("id", nextId);
        data.put("code", nextId);
        data.put("createdTime", now);
        data.put("createdBy", currentUser);
        data.put("version", "1.0");
        data.put("updatedTime", now);
        data.put("updatedBy", currentUser);

        log.debug("Auto-filled create fields: createTime={}, createBy={}", now, currentUser);
    }

    private void fillUpdateFields(CommandContext context) {
        Map<String, Object> data = context.getData();
        if (data == null) {
            data = new HashMap<>();
            context.setData(data);
        }

        LocalDateTime now = LocalDateTime.now();
        String currentUser = context.getAttribute("userId");

        data.put("updateTime", now);
        data.put("updateBy", currentUser);

        log.debug("Auto-filled update fields: updateTime={}, updateBy={}", now, currentUser);
    }
}
