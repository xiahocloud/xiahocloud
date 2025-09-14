package com.xiahou.yu.paaswebserver.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 动态命令响应DTO
 * @author wanghaoxin
 */
@Data
@Slf4j
public class DynamicCommandResponse {
    private Boolean success;
    private String message;
    private Map<String, Object> data;
    private String operationType;
    private Long affectedRows;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String toString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize DynamicCommandResponse to JSON", e);
            return super.toString();
        }
    }

}
