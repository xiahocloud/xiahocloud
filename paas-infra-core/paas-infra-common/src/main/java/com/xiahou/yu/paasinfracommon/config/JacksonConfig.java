package com.xiahou.yu.paasinfracommon.config;

/**
 * description:
 *
 * @author wanghaoxin
 * date     2025/9/22 13:49
 * @version 1.0
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiahou.yu.paasinfracommon.utils.ObjectMapperUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 标记这是一个Spring配置类
public class JacksonConfig {

    @Bean // 标记这个方法返回一个Spring Bean
    public ObjectMapper objectMapper() {
        return ObjectMapperUtils.newObjectMapper();
    }
}

