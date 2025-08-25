package com.xiahou.yu.paasdomincore.runtime.service;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import com.xiahou.yu.paasdomincore.design.executor.DataOperationExecutor;
import com.xiahou.yu.paasdomincore.design.service.DataOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 数据操作服务实现
 * 基于插件化架构和职责链模式的统一数据操作服务
 * 系统级参数从HTTP头和线程上下文获取
 *
 * @author xiahou
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultDataOperationService implements DataOperationService {

    private final DataOperationExecutor dataOperationExecutor;

    @Override
    public Object create(CommandContext context) {
        return execute(context, CommandType.CREATE);
    }

    @Override
    public Object update(CommandContext context) {
        return execute(context, CommandType.UPDATE);
    }

    @Override
    public Object delete(CommandContext context) {
        return execute(context, CommandType.DELETE);
    }

    @Override
    public Object query(CommandContext context) {
        return execute(context, CommandType.QUERY);
    }

    @Override
    public Object execute(CommandContext context, CommandType commandType) {
        // 将命令类型添加到上下文中，供处理器使用
        context.setAttribute("commandType", commandType.name());

        // 从当前线程上下文获取系统级参数并添加到 CommandContext 的 attributes 中
        // 注意：这里需要通过反射或其他方式获取线程上下文，因为这个模块不应该直接依赖 web 模块
        // 暂时使用日志记录，实际应该通过依赖注入或事件机制获取
        log.info("Executing {} operation for entity: {}",
                commandType, context.getEntity());

        return dataOperationExecutor.execute(context, commandType);
    }

    @Override
    public CommandContext buildContext(String entity, Map<String, Object> data, Map<String, Object> conditions) {
        return CommandContext.builder()
                .entity(entity)
                .data(data)
                .conditions(conditions)
                .build();
    }
}
