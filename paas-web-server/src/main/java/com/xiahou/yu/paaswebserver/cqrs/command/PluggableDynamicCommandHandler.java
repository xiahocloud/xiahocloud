package com.xiahou.yu.paaswebserver.cqrs.command;

import com.xiahou.yu.paaswebserver.adapter.DynamicDataOperationAdapter;
import com.xiahou.yu.paaswebserver.dto.DynamicCommandResponse;
import com.xiahou.yu.paaswebserver.dto.input.DynamicCommandInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 基于插件化架构的动态命令处理器
 * 使用职责链模式和命令模式实现可扩展的数据操作
 * 系统级参数从HTTP头自动获取，不再需要在命令中传递
 *
 * @author xiahou
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PluggableDynamicCommandHandler implements DynamicCommandHandler {

    private final DynamicDataOperationAdapter dataOperationAdapter;

    @Override
    public DynamicCommandResponse handle(DynamicCommandInput input) {
        log.info("Handling dynamic command with pluggable architecture: entity={}, operation={}",
                input.getEntity(), input.getOperation());
        // 调用数据操作适配器
        DynamicDataOperationAdapter.DataOperationResult result = dataOperationAdapter.handleCommand(
                input.getEntity(), input.getOperation(), input.getData(), input.getConditions()
        );

        // 转换响应格式
        return convertToResponse(result);
    }

    private DynamicCommandResponse convertToResponse(DynamicDataOperationAdapter.DataOperationResult result) {
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setSuccess(result.isSuccess());  // 修正方法名：getSuccess() -> isSuccess()
        response.setMessage(result.getMessage());
        response.setData(result.getDataAsMap());  // 使用getDataAsMap()获取Map格式的数据
        // 注意：DataOperationResult中没有operationType和affectedRows字段，需要根据实际情况处理
        return response;
    }

    private DynamicCommandResponse createErrorResponse(String message) {
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
