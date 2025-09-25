package com.xiahou.yu.paasinfracommon.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


/**
 * description:
 *
 * @author wanghaoxin
 * date     2025/9/24 14:41
 * @version 1.0
 */
public class ObjectMapperUtils {

    public static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 注册 JavaTimeModule，以支持 Java 8 日期时间类型（LocalDateTime, LocalDate, ZonedDateTime等）
        objectMapper.registerModule(new JavaTimeModule());
        
        // 配置Jackson处理JPA @Transient和@JsonProperty组合注解
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public boolean hasIgnoreMarker(AnnotatedMember m) {
                // 如果字段同时有JPA @Transient和@JsonProperty注解，不忽略该字段
                if (m.hasAnnotation(JsonProperty.class)) {
                    return false;
                }
                return super.hasIgnoreMarker(m);
            }
        });

        // 自动发现其余模块（如后续引入 kotlin / jdk8 模块）
        objectMapper.findAndRegisterModules();

        // **推荐的额外配置：**

        // 禁用将日期/时间序列化为时间戳（通常期望 ISO 8601 字符串格式，如 "yyyy-MM-ddTHH:mm:ss"）
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 如果在 JSON 中遇到未知属性，不抛出异常。这在处理部分更新或兼容未来数据模型时很有用。
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 如果 POJO 中没有属性用于某些 JSON 字段，也不抛出异常。
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // 设置日期格式，但对于 Java 8 Date/Time API，JavaTimeModule 默认处理 ISO 8601。
        // 如果你仍需要自定义 SimpleDateFormat，可以取消注释并调整：
        // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // objectMapper.setDateFormat(df);

        return objectMapper;
    }
}
