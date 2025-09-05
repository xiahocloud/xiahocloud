package com.xiahou.yu.paasdomincore.runtime.strategy;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;
import com.xiahou.yu.paasdomincore.design.registry.EntityTypeRegister;

/**
 * 实体执行器接口
 * 根据实体类型分发执行不同的处理逻辑
 *
 * @author xiahou
 */
public interface EntityExecutor {

    /**
     * 根据实体类型执行相应的处理逻辑
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    default Object executeByEntityType(CommandContext context) {
        String entity = context.getEntityName();
        EntityTypeEnum entityType = EntityTypeRegister.getEntityType(entity);

        if (EntityTypeEnum.META_ENTITY == entityType) {
            return metaEntityExecute(context);
        } else if (EntityTypeEnum.STD_ENTITY == entityType) {
            return stdEntityExecute(context);
        } else {
            return customEntityExecute(context);
        }
    }

    /**
     * 执行元数据实体操作
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    Object metaEntityExecute(CommandContext context);

    /**
     * 执行标准实体操作
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    Object stdEntityExecute(CommandContext context);

    /**
     * 执行自定义实体操作
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    Object customEntityExecute(CommandContext context);
}
