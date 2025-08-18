package com.xiahou.yu.paaswebserver.cqrs.command;

import com.xiahou.yu.paaswebserver.dto.input.DynamicCommandInput;
import com.xiahou.yu.paaswebserver.dto.DynamicCommandResponse;
import com.xiahou.yu.paaswebserver.entity.DynamicSchemaMetadata;
import com.xiahou.yu.paaswebserver.service.DynamicSchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态命令处理器
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicCommandHandler {

    private final DynamicSchemaService schemaService;

    /**
     * 处理动态命令
     */
    public DynamicCommandResponse handle(DynamicCommandInput input) {
        log.debug("Processing dynamic command: {}", input);

        // 验证输入
        validateCommandInput(input);

        // 获取Schema元数据
        DynamicSchemaMetadata metadata = getSchemaMetadata(input);

        // 执行命令
        return executeCommand(input, metadata);
    }

    private void validateCommandInput(DynamicCommandInput input) {
        if (input.getSystem() == null || input.getModule() == null ||
            input.getEntity() == null || input.getOperation() == null) {
            throw new IllegalArgumentException("System, module, entity, and operation are required");
        }

        if ("CREATE".equals(input.getOperation()) || "UPDATE".equals(input.getOperation())) {
            if (input.getData() == null || input.getData().isEmpty()) {
                throw new IllegalArgumentException("Data is required for CREATE/UPDATE operations");
            }
        }

        if ("UPDATE".equals(input.getOperation()) || "DELETE".equals(input.getOperation())) {
            if (input.getConditions() == null || input.getConditions().isEmpty()) {
                throw new IllegalArgumentException("Conditions are required for UPDATE/DELETE operations");
            }
        }
    }

    private DynamicSchemaMetadata getSchemaMetadata(DynamicCommandInput input) {
        return schemaService.getSchema(input.getSystem(), input.getModule(),
                                     input.getContext(), input.getApp(),
                                     input.getAggr(), input.getEntity());
    }

    private DynamicCommandResponse executeCommand(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setOperationType(input.getOperation());

        try {
            switch (input.getOperation().toUpperCase()) {
                case "CREATE":
                    return handleCreate(input, metadata);
                case "UPDATE":
                    return handleUpdate(input, metadata);
                case "DELETE":
                    return handleDelete(input, metadata);
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + input.getOperation());
            }
        } catch (Exception e) {
            log.error("Error executing command", e);
            response.setSuccess(false);
            response.setMessage("Error: " + e.getMessage());
            return response;
        }
    }

    private DynamicCommandResponse handleCreate(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
        // TODO: 实现实际的创建逻辑
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setSuccess(true);
        response.setMessage("Entity created successfully");
        response.setOperationType("CREATE");
        response.setAffectedRows(1L);

        // 返回创建的数据（包含生成的ID）
        Map<String, Object> createdData = new HashMap<>(input.getData());
        createdData.put("id", generateId());
        response.setData(createdData);

        return response;
    }

    private DynamicCommandResponse handleUpdate(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
        // TODO: 实现实际的更新逻辑
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setSuccess(true);
        response.setMessage("Entity updated successfully");
        response.setOperationType("UPDATE");
        response.setAffectedRows(1L);
        response.setData(input.getData());

        return response;
    }

    private DynamicCommandResponse handleDelete(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
        // TODO: 实现实际的删除逻辑
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setSuccess(true);
        response.setMessage("Entity deleted successfully");
        response.setOperationType("DELETE");
        response.setAffectedRows(1L);

        return response;
    }

    private String generateId() {
        return "generated_" + System.currentTimeMillis();
    }
}
