package com.xiahou.yu.paasdomincore.design.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiahou.yu.paasdomincore.design.dto.DynamicDataObject;

import java.io.IOException;
import java.util.Map;

/**
 * DynamicDataObject 的自定义 Jackson Deserializer。
 * <p>
 * 这个 Deserializer 解决了 GraphQL 输入中数据嵌套在 "record" 字段内的问题。
 * 它会智能地提取 "record" 对象的内容，并用它来创建 DynamicDataObject 实例。
 *
 * @author xiahou
 */
public class DynamicDataObjectDeserializer extends JsonDeserializer<DynamicDataObject> {

    private static final String RECORD_FIELD = "record";

    @Override
    public DynamicDataObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode rootNode = mapper.readTree(p);

        // 检查顶层是否包含 "record" 字段
        if (rootNode.has(RECORD_FIELD) && rootNode.get(RECORD_FIELD).isObject()) {
            JsonNode recordNode = rootNode.get(RECORD_FIELD);
            // 使用 "record" 节点的内容来创建 DynamicDataObject
            Map<String, Object> recordMap = mapper.convertValue(recordNode, Map.class);
            return DynamicDataObject.fromMap(recordMap);
        }

        // 如果没有 "record" 字段，或者它不是一个对象，
        // 这提供了灵活性，允许在不同场景下使用该 DTO。
        Map<String, Object> dataMap = mapper.convertValue(rootNode, Map.class);
        return DynamicDataObject.fromMap(dataMap);
    }
}

