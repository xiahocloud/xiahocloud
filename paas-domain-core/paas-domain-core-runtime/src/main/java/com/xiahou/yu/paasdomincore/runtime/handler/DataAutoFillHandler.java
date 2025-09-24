package com.xiahou.yu.paasdomincore.runtime.handler;

import com.xiahou.yu.paasdomincore.common.snowflake.SnowflakeIdGenerator;
import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;

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
        if (CollectionUtils.isEmpty(context.getRecords())) {
            log.error("No records found");
            throw new PaaSException(ResultStatusEnum.PARAMS_EMPTY);
        }

        for (DynamicDataObject record : context.getRecords()) {
            if (record == null) {
                log.warn("Record is null, skip auto-fill");
                continue;
            }

            LocalDateTime now = LocalDateTime.now();
            String currentUser = context.getRequestContext().getUserId();
            String tenantId = context.getRequestContext().getTenantId();

            long nextId = snowflakeIdGenerator.nextId();
            record.put("id", nextId);
            record.put("code", nextId);
            record.put("createdTime", now);
            record.put("creator", currentUser);
            record.put("version", "1");
            record.put("updatedTime", now);
            record.put("updater", currentUser);
            record.put("key", snowflakeIdGenerator.nextHexId(nextId));
            record.put("tenant", tenantId);
            record.put("isNew", true);
            // 自动补充 AbstractModel 其他字段
            if (!record.containsKey("status")) {
                record.put("status", 1);
            }
            if (!record.containsKey("enable")) {
                record.put("enable", 1);
            }
            if (!record.containsKey("visible")) {
                record.put("visible", 1);
            }
            if (!record.containsKey("sys")) {
                record.put("sys", 0);
            }
            log.debug("Auto-filled create fields: id={}, code={}, createdTime={}, creator={}, tenantId={}",
                    nextId, nextId, now, currentUser, tenantId);
        }
    }

    private void fillUpdateFields(CommandContext context) {
        if (CollectionUtils.isEmpty(context.getRecords())) {
            log.error("No records found");
            throw new PaaSException(ResultStatusEnum.PARAMS_EMPTY);
        }

        for (DynamicDataObject record : context.getRecords()) {
            if (record == null) {
                log.warn("Record is null, skip auto-fill");
                continue;
            }
            LocalDateTime now = LocalDateTime.now();
            String currentUser = context.getAttribute("userId");

            record.put("updateTime", now);
            record.put("updater", currentUser);
            record.put("isNew", false);

            log.debug("Auto-filled update fields: updateTime={}, updater={}", now, currentUser);
        }

    }
}
