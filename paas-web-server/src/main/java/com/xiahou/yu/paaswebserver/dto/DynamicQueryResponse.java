package com.xiahou.yu.paaswebserver.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiahou.yu.paasinfracommon.utils.ObjectMapperUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 动态查询响应DTO
 */
@Data
@Slf4j
public class DynamicQueryResponse implements Serializable {

    private String code;
    private String message;
    private Object data;
    private String traceId;
    private String version;
    private String schema;

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtils.newObjectMapper();;

    @Override
    public String toString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize DynamicQueryResponse to JSON", e);
            return super.toString();
        }
    }
}
