package com.xiahou.yu.paaswebserver.cqrs.command;

import com.xiahou.yu.paaswebserver.context.RequestContextHolder;
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
        // 从线程上下文获取租户信息
        String contextTenantId = RequestContextHolder.getTenantId();
        String contextUserId = RequestContextHolder.getUserId();
        String contextRequestId = RequestContextHolder.getRequestId();

        log.debug("Processing dynamic command: {} - Context: tenantId={}, userId={}, requestId={}",
                 input, contextTenantId, contextUserId, contextRequestId);

        // 验证输入
        validateCommandInput(input);

        // 如果输入中没有指定租户，使用上下文中的租户ID
        if (input.getSystem() == null && contextTenantId != null) {
            input.setSystem(contextTenantId);
        }

        // 获取Schema元数据
        DynamicSchemaMetadata metadata = getSchemaMetadata(input);

        // 在数据中自动添加租户和用户信息
        enrichCommandData(input, contextTenantId, contextUserId);

        // 执行命令
        DynamicCommandResponse response = executeCommand(input, metadata);

        log.info("Dynamic command completed: requestId={}, operation={}, tenantId={}, userId={}",
                contextRequestId, input.getOperation(), contextTenantId, contextUserId);

        return response;
    }

    private void validateCommandInput(DynamicCommandInput input) {
        String contextTenantId = RequestContextHolder.getTenantId();

        if (input.getSystem() == null && contextTenantId == null) {
            throw new IllegalArgumentException("System is required when no tenant context available");
        }

        if (input.getModule() == null || input.getEntity() == null || input.getOperation() == null) {
            throw new IllegalArgumentException("Module, entity, and operation are required");
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

    /**
     * 为命令数据添加上下文信息
     */
    private void enrichCommandData(DynamicCommandInput input, String tenantId, String userId) {
        if (input.getData() == null) {
            input.setData(new HashMap<>());
        }

        // 自动添加租户信息
        if (tenantId != null) {
            input.getData().put("tenantId", tenantId);
        }

        // 根据操作类型添加审计信息
        long currentTime = System.currentTimeMillis();
        if ("CREATE".equals(input.getOperation())) {
            if (userId != null) {
                input.getData().put("createdBy", userId);
            }
            input.getData().put("createdAt", currentTime);
            input.getData().put("updatedAt", currentTime);
            if (userId != null) {
                input.getData().put("updatedBy", userId);
            }
        } else if ("UPDATE".equals(input.getOperation())) {
            if (userId != null) {
                input.getData().put("updatedBy", userId);
            }
            input.getData().put("updatedAt", currentTime);
        }
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
            String requestId = RequestContextHolder.getRequestId();
            log.error("Error executing command: requestId={}, operation={}", requestId, input.getOperation(), e);
            response.setSuccess(false);
            response.setMessage("Error: " + e.getMessage());
            return response;
        }
    }

    private DynamicCommandResponse handleCreate(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
        // TODO: 实现实际的创建逻辑，确保租户隔离
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setSuccess(true);
        response.setMessage("Entity created successfully");
        response.setOperationType("CREATE");
        response.setAffectedRows(1L);

        // 返回创建的数据（包含生成的ID和租户信息）
        Map<String, Object> createdData = new HashMap<>(input.getData());
        createdData.put("id", generateId());
        response.setData(createdData);

        return response;
    }

    private DynamicCommandResponse handleUpdate(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
        // TODO: 实现实际的更新逻辑，确保租户隔离
        DynamicCommandResponse response = new DynamicCommandResponse();
        response.setSuccess(true);
        response.setMessage("Entity updated successfully");
        response.setOperationType("UPDATE");
        response.setAffectedRows(1L);
        response.setData(input.getData());

        return response;
    }

    private DynamicCommandResponse handleDelete(DynamicCommandInput input, DynamicSchemaMetadata metadata) {
        // TODO: 实现实际的删除逻辑，确保租户隔离
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
