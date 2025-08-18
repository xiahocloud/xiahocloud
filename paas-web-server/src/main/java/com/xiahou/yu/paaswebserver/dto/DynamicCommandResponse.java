package com.xiahou.yu.paaswebserver.dto;

import lombok.Data;
import java.util.Map;

/**
 * 动态命令响应DTO
 */
@Data
public class DynamicCommandResponse {
    private Boolean success;
    private String message;
    private Map<String, Object> data;
    private String operationType;
    private Long affectedRows;
}
