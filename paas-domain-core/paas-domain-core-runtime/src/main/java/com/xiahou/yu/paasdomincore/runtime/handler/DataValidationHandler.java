package com.xiahou.yu.paasdomincore.runtime.handler;

import com.xiahou.yu.paasdomincore.design.chain.Handler;
import com.xiahou.yu.paasdomincore.design.chain.HandlerChain;
import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 数据验证处理器
 * 在数据操作前进行验证
 *
 * @author xiahou
 */
@Component
@Slf4j
public class DataValidationHandler implements Handler {

    @Override
    public boolean handle(CommandContext context, HandlerChain chain) {
        String aggr = context.getAttribute("aggr");
        log.info("Executing data validation for {}.{}", aggr, context.getEntityName());

        // 基础验证逻辑
        if (!validateContext(context)) {
            log.error("Data validation failed for {}.{}", aggr, context.getEntityName());
            return false;
        }

        // 业务数据验证
        if (!validateBusinessData(context)) {
            log.error("Business data validation failed for {}.{}", aggr, context.getEntityName());
            return false;
        }

        log.info("Data validation passed for {}.{}", aggr, context.getEntityName());
        return chain.proceed(context);
    }

    @Override
    public String getName() {
        return "DataValidationHandler";
    }

    @Override
    public int getOrder() {
        return 100; // 高优先级，最先执行
    }

    @Override
    public boolean supports(CommandContext context) {
        // 支持所有非查询操作
        return !CollectionUtils.isEmpty(context.getRecords()) &&
               !CommandType.QUERY.name().equals(context.getAttribute("commandType"));
    }

    private boolean validateContext(CommandContext context) {
        String system = context.getRequestContext().getSystem();
        String module = context.getRequestContext().getModule();
        return system != null && 
               module != null && 
               context.getEntityName() != null;
    }

    private boolean validateBusinessData(CommandContext context) {
        // 这里可以根据实体类型进行具体的业务验证
        // 例如：必填字段检查、数据格式验证等
        return !CollectionUtils.isEmpty(context.getRecords());
    }
}
