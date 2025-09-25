package com.xiahou.yu.paaswebserver.adapter;

import com.xiahou.yu.paasdomincore.design.command.CommandContext;
import com.xiahou.yu.paasdomincore.design.command.CommandType;
import com.xiahou.yu.paasdomincore.design.dto.QueryResult;
import com.xiahou.yu.paasdomincore.design.service.DataOperationService;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import com.xiahou.yu.paasdomincore.design.filter.Filter;
import com.xiahou.yu.paasinfracommon.context.RequestContext;
import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public DataOperationResult handleCommand(CommandContext commandContext) {
        return handleCommand(commandContext.getEntityName(), commandContext.getOperation(), commandContext.getRecords(), commandContext.getFilter(), commandContext.getRequestContext());
    }

    /**
     * 处理动态查询操作
     */
    public DataOperationResult handleQuery(String entity, Filter filter, List<String> fields, Integer page, Integer size, List<String> orderBy, RequestContext requestContext) {
        // 从线程上下文获取系统级参数
        String system = RequestContextHolder.getSystem();
        String module = RequestContextHolder.getModule();
        String context = RequestContextHolder.getContextId();
        String app = RequestContextHolder.getApp();
        String aggr = RequestContextHolder.getAggr();
        String tenantId = RequestContextHolder.getTenantId();
        String userId = RequestContextHolder.getUserId();
        String requestId = RequestContextHolder.getRequestId();

        log.info("Processing dynamic query: entity={}, tenantId={}, userId={}, requestId={}",
                entity, tenantId, userId, requestId);

        try {
            // 创建查询上下文
            CommandContext queryContext = CommandContext.builder()
                    .entityName(entity)
                    .filter(filter)
                    .requestContext(requestContext)
                    .build();

            // 添加查询特定的属性
            queryContext.setAttribute("fields", fields);
            queryContext.setAttribute("page", page);
            queryContext.setAttribute("size", size);
            queryContext.setAttribute("orderBy", orderBy);

            // 执行查询操作
            QueryResult result = (QueryResult) dataOperationService.execute(queryContext, CommandType.QUERY);
            // 将结果包装为DynamicDataObject
            List<>
            DynamicDataObject resultData = convertToDataObject(result);

            log.info("Dynamic query completed successfully: entity={}, requestId={}",
                    entity, requestId);

            return new DataOperationResult(true, resultData, "Query completed successfully", null);

        } catch (Exception e) {
            log.error("Error processing dynamic query: entity={}, requestId={}",
                     entity, requestId, e);

            return new DataOperationResult(false, DynamicDataObject.empty(),
                    "Query failed: " + e.getMessage(), e.getClass().getSimpleName());
        }
    }


    /**
     * 处理动态数据操作命令 - 使用Filter替代conditions参数
     */
    public DataOperationResult handleCommand(String entity, String operation, List<DynamicDataObject> records, Filter filter, RequestContext requestContext) {

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
            // 创建命令上下文 - 使用统一的Filter系统
            CommandContext commandContext = CommandContext.builder()
                    .entityName(entity)
                    .records(records)
                    .filter(filter)
                    .requestContext(requestContext)
                    .build();

            // 解析操作类型
            CommandType commandType = parseCommandType(operation);

            // 执行操作
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
        private List<DynamicDataObject> records;
        private String message;
        private String error;

        // 构造器
        public DataOperationResult(boolean success, List<DynamicDataObject> records, String message, String error) {
            this.success = success;
            this.records = records;
            this.message = message;
            this.error = error;
        }


        /**
         * 获取数据的Map表示（用于向后兼容）
         */
        public Map<String, Object> getDataAsMap() {
            return records != null ? records.toMap() : null;
        }

        /**
         * 检查是否有数据
         */
        public boolean hasData() {
            return records != null && !records.isEmpty();
        }

        /**
         * 获取特定属性值
         */
        public Object getDataValue(String property) {
            return records != null ? records.getValue(property) : null;
        }

        /**
         * 获取字符串类型的数据值
         */
        public String getDataString(String property) {
            return records != null ? records.getString(property) : null;
        }

        /**
         * 获取整数类型的数据值
         */
        public Integer getDataInteger(String property) {
            return records != null ? records.getInteger(property) : null;
        }
    }
}
