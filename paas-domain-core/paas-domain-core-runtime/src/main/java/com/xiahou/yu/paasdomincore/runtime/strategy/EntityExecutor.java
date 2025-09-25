package com.xiahou.yu.paasdomincore.runtime.strategy;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.constant.EntityTypeEnum;
import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;
import com.xiahou.yu.paasdomincore.design.registry.EntityRegistryManager;

/**
 * 实体执行器接口
 * 根据实体类型分发执行不同的处理逻辑
 *
 * @author xiahou
 */
public interface EntityExecutor {

    /**
     * 获取实体注册管理器实例
     *
     * @return EntityRegistryManager实例
     */
    EntityRegistryManager getEntityRegistryManager();

    /**
     * 根据实体类型执行相应的处理逻辑
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    default DataOperationResult executeByEntityType(CommandContext context) {
        String entity = context.getEntityName();
        EntityTypeEnum entityType = getEntityRegistryManager().getEntityType(entity);

        if (EntityTypeEnum.META_ENTITY == entityType) {
            return metaEntityExecute(context);
        } else if (EntityTypeEnum.SYSTEM_ENTITY == entityType) {
            return systemEntityExecute(context);
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
    DataOperationResult metaEntityExecute(CommandContext context);

    /**
     * 执行标准实体操作
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    DataOperationResult systemEntityExecute(CommandContext context);

    /**
     * 执行自定义实体操作
     *
     * @param context 命令上下文
     * @return 执行结果
     */
    DataOperationResult customEntityExecute(CommandContext context);
}
