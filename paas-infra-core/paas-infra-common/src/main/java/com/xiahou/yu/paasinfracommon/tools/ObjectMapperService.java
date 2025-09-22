package com.xiahou.yu.paasinfracommon.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2025/9/22 13:58
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ObjectMapperService {
    private final ObjectMapper objectMapper;

    public <T> T convertToEntity(String entityName, Map<String, Object> data, Class<T> clazz) {
        try {
            return objectMapper.convertValue(data, clazz);
        } catch (Exception e) {
            log.error("Error converting data to entity {}: {}", entityName, e.getMessage(), e);
            return null;
        }
    }

    public <T> T convertToEntity(Map<String, Object> data, Class<T> clazz) {
        String entityName = clazz.getName();
        try {
            return convertToEntity(entityName, data, clazz);
        } catch (Exception e) {
            log.error("Error converting data to entity {}: {}", entityName, e.getMessage(), e);
            return null;
        }
    }
}
