package com.xiahou.yu.paaswebserver.cqrs.query;

import com.xiahou.yu.paaswebserver.dto.input.DynamicQueryInput;
import com.xiahou.yu.paaswebserver.dto.DynamicQueryResponse;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import com.xiahou.yu.paaswebserver.entity.DynamicSchemaMetadata;
import com.xiahou.yu.paaswebserver.service.DynamicSchemaService;
import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 动态查询处理器
 * 使用DynamicDataObject替代Map<String, Object>，提供更优雅的数据访问
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
        // 从线程上下文获取系统级参数和用户信息
        String system = RequestContextHolder.getSystem();
        String module = RequestContextHolder.getModule();
        String context = RequestContextHolder.getContextId();
        String app = RequestContextHolder.getApp();
        String aggr = RequestContextHolder.getAggr();
        String contextTenantId = RequestContextHolder.getTenantId();
        String contextUserId = RequestContextHolder.getUserId();
        String contextRequestId = RequestContextHolder.getRequestId();

        log.debug("Processing dynamic query: entity={} - Context: system={}, module={}, tenantId={}, userId={}, requestId={}",
                 input.getEntityName(), system, module, contextTenantId, contextUserId, contextRequestId);

        // 验证输入
        validateQueryInput(input, system, module);

        // 获取Schema元数据
        DynamicSchemaMetadata metadata = getSchemaMetadata(system, module, context, app, aggr, input.getEntityName());

        // 构建查询，传入上下文信息 - 现在返回DynamicDataObject列表
        List<DynamicDataObject> data = executeQuery(input, metadata, contextTenantId);

        // 构建响应 - 转换为Map以保持API兼容性
        DynamicQueryResponse response = new DynamicQueryResponse();
        response.setData(convertToMapList(data));
        response.setTotal((long) data.size());
        response.setPage(input.getPage() != null ? input.getPage() : 0);
        response.setSize(input.getSize() != null ? input.getSize() : data.size());
        response.setHasNext(false);
        response.setSchema(buildSchemaString(input, metadata));
        response.setFieldTypes(metadata.getFieldTypes());

        log.info("Dynamic query completed: requestId={}, resultCount={}, tenantId={}",
                contextRequestId, data.size(), contextTenantId);

        return response;
    }

    /**
     * 验证查询输入
     */
    private void validateQueryInput(DynamicQueryInput input, String system, String module) {
        // 验证系统级参数
        if (system == null) {
            throw new IllegalArgumentException("Missing system parameter in headers");
        }

        if (module == null || input.getEntityName() == null) {
            throw new IllegalArgumentException("Missing required parameters: module or entity");
        }
    }

    /**
     * 获取Schema元数据
     */
    private DynamicSchemaMetadata getSchemaMetadata(String system, String module, String context, String app, String aggr, String entity) {
        return schemaService.getSchema(system, module, context, app, aggr, entity);
    }

    /**
     * 执行查询 - 返回DynamicDataObject列表，提供更优雅的数据访问
     */
    private List<DynamicDataObject> executeQuery(DynamicQueryInput input, DynamicSchemaMetadata metadata, String tenantId) {
        log.debug("Executing query for schema: {} with tenantId: {}", metadata.getEntity(), tenantId);

        List<DynamicDataObject> result = new ArrayList<>();

        // 使用DynamicDataObject创建模拟数据 - 展示优雅的数据构建方式
        DynamicDataObject record1 = DynamicDataObject.empty()
                .setValue("id", "1")
                .setValue("name", "张三")
                .setValue("email", "zhangsan@example.com")
                .setValue("age", 25)
                .setValue("tenantId", tenantId)
                .setValue("createdAt", "2024-01-01T10:00:00Z");

        DynamicDataObject record2 = DynamicDataObject.empty()
                .setValue("id", "2")
                .setValue("name", "李四")
                .setValue("email", "lisi@example.com")
                .setValue("age", 30)
                .setValue("tenantId", tenantId)
                .setValue("createdAt", "2024-01-02T10:00:00Z");

        // 展示嵌套对象的优雅处理
        DynamicDataObject userProfile = DynamicDataObject.empty()
                .setValue("department", "技术部")
                .setValue("position", "高级工程师")
                .setValue("salary", 15000);

        record1.setValue("profile", userProfile.toMap());

        result.add(record1);
        result.add(record2);

        // 如果指定了字段，则过滤返回的字段 - 使用DynamicDataObject的优雅API
        if (input.getFields() != null && !input.getFields().isEmpty()) {
            return result.stream().map(record -> {
                DynamicDataObject filteredRecord = DynamicDataObject.empty();
                for (String field : input.getFields()) {
                    if (record.hasProperty(field)) {
                        filteredRecord.setValue(field, record.getValue(field));
                    }
                }
                return filteredRecord;
            }).toList();
        }

        return result;
    }

    /**
     * 将DynamicDataObject列表转换为Map列表以保持API兼容性
     */
    private List<Map<String, Object>> convertToMapList(List<DynamicDataObject> dataObjects) {
        return dataObjects.stream()
                .map(DynamicDataObject::toMap)
                .toList();
    }

    private String buildSchemaString(DynamicQueryInput input, DynamicSchemaMetadata metadata) {
        // 从线程上下文获取系统级参数
        String system = RequestContextHolder.getSystem();
        String module = RequestContextHolder.getModule();
        String context = RequestContextHolder.getContextId();
        String app = RequestContextHolder.getApp();
        String aggr = RequestContextHolder.getAggr();

        return String.format("%s.%s.%s.%s.%s.%s",
            system, module, context, app, aggr, input.getEntityName());
    }
}
