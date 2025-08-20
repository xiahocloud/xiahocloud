package com.xiahou.yu.paaswebserver.cqrs.query;

import com.xiahou.yu.paaswebserver.context.RequestContextHolder;
import com.xiahou.yu.paaswebserver.dto.input.DynamicQueryInput;
import com.xiahou.yu.paaswebserver.dto.DynamicQueryResponse;
import com.xiahou.yu.paaswebserver.entity.DynamicSchemaMetadata;
import com.xiahou.yu.paaswebserver.service.DynamicSchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 动态查询处理器
 *
 * @author xiahou
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicQueryHandler {

    private final DynamicSchemaService schemaService;

    /**
     * 处理动态查询
     */
    public DynamicQueryResponse handle(DynamicQueryInput input) {
        // 从线程上下文获取租户信息
        String contextTenantId = RequestContextHolder.getTenantId();
        String contextUserId = RequestContextHolder.getUserId();
        String contextRequestId = RequestContextHolder.getRequestId();

        log.debug("Processing dynamic query: {} - Context: tenantId={}, userId={}, requestId={}",
                 input, contextTenantId, contextUserId, contextRequestId);

        // 验证输入
        validateQueryInput(input);

        // 如果输入中没有指定租户，使用上下文中的租户ID
        if (input.getSystem() == null && contextTenantId != null) {
            input.setSystem(contextTenantId);
        }

        // 获取Schema元数据
        DynamicSchemaMetadata metadata = getSchemaMetadata(input);

        // 构建查询，传入上下文信息
        List<Map<String, Object>> data = executeQuery(input, metadata, contextTenantId);

        // 构建响应
        DynamicQueryResponse response = new DynamicQueryResponse();
        response.setData(data);
        response.setTotal((long) data.size());
        response.setPage(input.getPage() != null ? input.getPage() : 0);
        response.setSize(input.getSize() != null ? input.getSize() : data.size());
        // TODO: 实现分页逻辑
        response.setHasNext(false);
        response.setSchema(buildSchemaString(input));
        response.setFieldTypes(metadata.getFieldTypes());

        log.info("Dynamic query completed: requestId={}, resultCount={}, tenantId={}",
                contextRequestId, data.size(), contextTenantId);

        return response;
    }

    private void validateQueryInput(DynamicQueryInput input) {
        String contextTenantId = RequestContextHolder.getTenantId();

        if (input.getSystem() == null && contextTenantId == null) {
            throw new IllegalArgumentException("System is required when no tenant context available");
        }

        if (input.getModule() == null || input.getEntity() == null) {
            throw new IllegalArgumentException("Module and entity are required");
        }
    }

    private DynamicSchemaMetadata getSchemaMetadata(DynamicQueryInput input) {
        return schemaService.getSchema(input.getSystem(), input.getModule(),
                                     input.getContext(), input.getApp(),
                                     input.getAggr(), input.getEntity());
    }

    private List<Map<String, Object>> executeQuery(DynamicQueryInput input, DynamicSchemaMetadata metadata, String tenantId) {
        // TODO: 实现实际的数据库查询逻辑，使用metadata进行Schema验证
        // 这里返回模拟数据，包含租户过滤
        log.debug("Executing query for schema: {} with tenantId: {}", metadata.getEntity(), tenantId);

        List<Map<String, Object>> result = new ArrayList<>();

        // 模拟数据 - 在实际实现中应该根据tenantId进行数据过滤
        Map<String, Object> record1 = new HashMap<>();
        record1.put("id", "1");
        record1.put("name", "张三");
        record1.put("email", "zhangsan@example.com");
        record1.put("age", 25);
        record1.put("tenantId", tenantId); // 添加租户信息
        record1.put("createdAt", "2024-01-01T10:00:00Z");

        Map<String, Object> record2 = new HashMap<>();
        record2.put("id", "2");
        record2.put("name", "李四");
        record2.put("email", "lisi@example.com");
        record2.put("age", 30);
        record2.put("tenantId", tenantId); // 添加租户信息
        record2.put("createdAt", "2024-01-02T10:00:00Z");

        result.add(record1);
        result.add(record2);

        // 如果指定了字段，则过滤返回的字段
        if (input.getFields() != null && !input.getFields().isEmpty()) {
            return result.stream().map(record -> {
                Map<String, Object> filteredRecord = new HashMap<>();
                for (String field : input.getFields()) {
                    if (record.containsKey(field)) {
                        filteredRecord.put(field, record.get(field));
                    }
                }
                return filteredRecord;
            }).toList();
        }

        return result;
    }

    private String buildSchemaString(DynamicQueryInput input) {
        return String.format("%s.%s.%s.%s.%s.%s",
            input.getSystem(),
            input.getModule(),
            input.getContext(),
            input.getApp(),
            input.getAggr(),
            input.getEntity());
    }
}
