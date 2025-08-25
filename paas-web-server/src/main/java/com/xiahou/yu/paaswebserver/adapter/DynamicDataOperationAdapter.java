package com.xiahou.yu.paaswebserver.adapter;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import com.xiahou.yu.paasdomincore.design.service.DataOperationService;
import com.xiahou.yu.paasdomincore.runtime.dto.DynamicDataObject;
import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 动态数据操作适配器
 * 使用DynamicDataObject提供更优雅的数据传输
 *
 * @author xiahou
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicDataOperationAdapter {

    private final DataOperationService dataOperationService;

    /**
     * 处理动态数据操作命令 - 使用DynamicDataObject替代Map参数
     */
    public DataOperationResult handleCommand(String entity, String operation, DynamicDataObject data,
                                             DynamicDataObject conditions) {

        // 从线程上下文获取系统级参数
        String system = RequestContextHolder.getSystem();
        String module = RequestContextHolder.getModule();
        String context = RequestContextHolder.getContextId();
        String app = RequestContextHolder.getApp();
        String aggr = RequestContextHolder.getAggr();
        String tenantId = RequestContextHolder.getTenantId();
        String userId = RequestContextHolder.getUserId();
        String requestId = RequestContextHolder.getRequestId();

        log.info("Processing dynamic operation: entity={}, operation={}, tenantId={}, userId={}, requestId={}",
                entity, operation, tenantId, userId, requestId);

        try {
            // 创建命令上下文 - 不再需要在上下文中传递系统级参数
            CommandContext commandContext = CommandContext.builder()
                    .entity(entity)
                    .data(data != null ? data.toMap() : null)
                    .conditions(conditions != null ? conditions.toMap() : null)
                    .build();

            // 解析操作类型
            CommandType commandType = parseCommandType(operation);

            // 执行操作 - 修正方法调用顺序
            Object result = dataOperationService.execute(commandContext, commandType);

            // 将结果包装为DynamicDataObject
            DynamicDataObject resultData = convertToDataObject(result);

            log.info("Dynamic operation completed successfully: entity={}, operation={}, requestId={}",
                    entity, operation, requestId);

            return new DataOperationResult(true, resultData, "Operation completed successfully", null);

        } catch (Exception e) {
            log.error("Error processing dynamic operation: entity={}, operation={}, requestId={}",
                     entity, operation, requestId, e);

            return new DataOperationResult(false, DynamicDataObject.empty(),
                    "Operation failed: " + e.getMessage(), e.getClass().getSimpleName());
        }
    }

    /**
     * 重载方法：支持传统的Map参数，内部转换为DynamicDataObject
     */
    public DataOperationResult handleCommand(String entity, String operation, Map<String, Object> data,
                                             Map<String, Object> conditions) {
        DynamicDataObject dataObject = data != null ? DynamicDataObject.fromMap(data) : null;
        DynamicDataObject conditionsObject = conditions != null ? DynamicDataObject.fromMap(conditions) : null;

        return handleCommand(entity, operation, dataObject, conditionsObject);
    }

    private CommandType parseCommandType(String operation) {
        try {
            return CommandType.valueOf(operation.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }

    /**
     * 将结果转换为DynamicDataObject
     */
    private DynamicDataObject convertToDataObject(Object result) {
        if (result == null) {
            return DynamicDataObject.empty();
        }

        if (result instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapResult = (Map<String, Object>) result;
            return DynamicDataObject.fromMap(mapResult);
        }

        // 对于其他类型，使用MetaObject的能力转换为DynamicDataObject
        return DynamicDataObject.fromObject(result);
    }

    /**
     * 数据操作结果
     */
    @Data
    public static class DataOperationResult {
        private boolean success;
        private DynamicDataObject data;
        private String message;
        private String error;

        // 构造器
        public DataOperationResult(boolean success, DynamicDataObject data, String message, String error) {
            this.success = success;
            this.data = data;
            this.message = message;
            this.error = error;
        }


        /**
         * 获取数据的Map表示（用于向后兼容）
         */
        public Map<String, Object> getDataAsMap() {
            return data != null ? data.toMap() : null;
        }

        /**
         * 检查是否有数据
         */
        public boolean hasData() {
            return data != null && !data.isEmpty();
        }

        /**
         * 获取特定属性值
         */
        public Object getDataValue(String property) {
            return data != null ? data.getValue(property) : null;
        }

        /**
         * 获取字符串类型的数据值
         */
        public String getDataString(String property) {
            return data != null ? data.getString(property) : null;
        }

        /**
         * 获取整数类型的数据值
         */
        public Integer getDataInteger(String property) {
            return data != null ? data.getInteger(property) : null;
        }
    }
}
