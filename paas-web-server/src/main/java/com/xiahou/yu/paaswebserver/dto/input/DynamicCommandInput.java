package com.xiahou.yu.paaswebserver.dto.input;

import lombok.Data;
import java.util.Map;

/**
 * 动态命令输入DTO
 */
@Data
public class DynamicCommandInput {
    private String system;
    private String module;
    private String context;
    private String app;
    private String aggr;
    private String entity;
    private String operation; // CREATE, UPDATE, DELETE
    private Map<String, Object> data;
    private Map<String, Object> conditions; // for UPDATE/DELETE
}
