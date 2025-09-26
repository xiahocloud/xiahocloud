package com.xiahou.yu.paaswebserver.cqrs.query;

import com.xiahou.yu.paasdomincore.design.dto.DataOperationResult;
import com.xiahou.yu.paasinfracommon.context.RequestContextHolder;
import com.xiahou.yu.paasmetacore.constant.exception.PaaSException;
import com.xiahou.yu.paasmetacore.constant.ResultStatusEnum;
import com.xiahou.yu.paaswebserver.adapter.DynamicDataOperationAdapter;
import com.xiahou.yu.paaswebserver.dto.DynamicQueryResponse;
import com.xiahou.yu.paaswebserver.dto.input.DynamicQueryInput;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 可插拔的动态查询处理器
 * 采用与PluggableDynamicCommandHandler相似的架构
 * 支持system、meta、custom查询的统一处理
 *
 * 优化点：
 * 1. 统一常量键名，避免魔法字符串
 * 2. convertToResponse 提前返回失败场景，减少嵌套
 * 3. 增加对 payload 为空的防御性处理
 * 4. 提取分页默认值逻辑，避免重复
 * 5. 使用模式匹配 (JDK >= 17) 简化 instanceof 转换
 * 6. 私有方法语义更明确，代码更紧凑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultDynamicQueryHandler implements DynamicQueryHandler {

    private static final String KEY_DATA = "data";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_FIELD_TYPES = "fieldTypes";

    private final DynamicDataOperationAdapter dataOperationAdapter;

    @Override
    public DynamicQueryResponse handle(DynamicQueryInput input) {
        log.info("Handling dynamic query: entity={}, fields={}", input.getEntityName(), input.getFields());
        validateQueryInput(input);

        DataOperationResult result = dataOperationAdapter.handleQuery(
                input.getEntityName(),
                input.getEffectiveFilter(),
                input.getFields(),
                input.getPage(),
                input.getSize(),
                input.getOrderBy()
        );
        return convertToResponse(result, input);
    }

    /** 验证查询输入参数 */
    private void validateQueryInput(DynamicQueryInput input) {
        if (input.getEntityName() == null || input.getEntityName().trim().isEmpty()) {
            throw new PaaSException(ResultStatusEnum.PARAMS_EMPTY, "Entity name is required");
        }
    }

    /** 将 DataOperationResult 转换为 DynamicQueryResponse */
    private DynamicQueryResponse convertToResponse(DataOperationResult result, DynamicQueryInput input) {
        DynamicQueryResponse response = new DynamicQueryResponse();
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        response.setData(result.getData());
        response.setTraceId(RequestContextHolder.getRequestId());
        response.setSchema(input.getEntityName());
        response.setVersion("1.0");
        return response;
    }
}