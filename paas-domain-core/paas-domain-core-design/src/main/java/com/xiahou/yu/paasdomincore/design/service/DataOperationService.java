package com.xiahou.yu.paasdomincore.design.service;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;

import java.util.List;
import java.util.Map;

/**
 * 数据操作服务接口
 * 对外提供统一的数据操作API
 * 系统级参数(system, module, context, app, aggr)现在从HTTP头和线程上下文获取
 *
 * @author xiahou
 */
public interface DataOperationService {

    /**
     * 创建数据
     * @param context 命令上下文
     * @return 操作结果
     */
    Object create(CommandContext context);

    /**
     * 更新数据
     * @param context 命令上下文
     * @return 操作结果
     */
    Object update(CommandContext context);

    /**
     * 删除数据
     * @param context 命令上下文
     * @return 操作结果
     */
    Object delete(CommandContext context);

    /**
     * 查询数据
     * @param context 命令上下文
     * @return 查询结果
     */
    Object query(CommandContext context);

    /**
     * 通用数据操作方法
     * @param context 命令上下文
     * @param commandType 命令类型
     * @return 操作结果
     */
    Object execute(CommandContext context, CommandType commandType);

    /**
     * 构建命令上下文的便捷方法
     * 系统级参数从线程上下文自动获取
     * @param entity 实体标识
     * @param records 数据
     * @param conditions 条件
     * @return 命令上下文
     */
    CommandContext buildContext(String entity, List<DynamicDataObject> records, Map<String, Object> conditions);
}
