package com.xiahou.yu.paasdomincore.runtime.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * Runtime 模块配置类
 * 处理跨模块依赖和可选组件的配置
 *
 * @author xiahou
 */
@Configuration
@Slf4j
public class RuntimeConfiguration {

    /**
     * 创建一个占位符的 RepositoryManager，当设计模块的 RepositoryManager 不可用时使用
     */
    @Bean
    @ConditionalOnMissingBean(name = "repositoryManager")
    public Object placeholderRepositoryManager() {
        log.warn("RepositoryManager from design module not found, creating placeholder");
        return new Object() {
            public boolean hasRepository(String entityName) {
                return false;
            }
        };
    }
}
