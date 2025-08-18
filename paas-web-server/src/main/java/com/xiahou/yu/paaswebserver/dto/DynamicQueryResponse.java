package com.xiahou.yu.paaswebserver.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 动态查询响应DTO
 */
@Data
public class DynamicQueryResponse {
    private List<Map<String, Object>> data;
    private Long total;
    private Integer page;
    private Integer size;
    private Boolean hasNext;
    private String schema;
    private Map<String, String> fieldTypes;
}
