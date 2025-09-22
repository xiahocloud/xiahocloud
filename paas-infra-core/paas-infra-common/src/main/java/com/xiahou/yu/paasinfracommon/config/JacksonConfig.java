package com.xiahou.yu.paasinfracommon.config;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2025/9/22 13:49
 * @version 1.0
 */
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 标记这是一个Spring配置类
public class JacksonConfig {

    @Bean // 标记这个方法返回一个Spring Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 注册 JavaTimeModule，以支持 Java 8 日期时间类型（LocalDateTime, LocalDate, ZonedDateTime等）
        objectMapper.registerModule(new JavaTimeModule());

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

