package com.xiahou.yu.paaswebserver.dto.input;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 动态查询输入DTO
 */
@Data
public class DynamicQueryInput {
    private String system;
    private String module;
    private String context;
    private String app;
    private String aggr;
    private String entity;
    private String filter;
    private List<String> fields;
    private Map<String, Object> filterParams;
    private Integer page;
    private Integer size;
    private List<String> orderBy;
}
