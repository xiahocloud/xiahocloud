package com.xiahou.yu.paaswebserver.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiahou.yu.paasinfracommon.utils.ObjectMapperUtils;
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
    private String code;
    private String message;
    private Object data;
    private String operationType;
    private Long affectedRows;

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtils.newObjectMapper();;

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
