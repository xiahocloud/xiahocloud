package com.xiahou.yu.paasmetacore.core;

import com.xiahou.yu.paasmetacore.metadata.PropertyDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 全局属性池管理器
 * 根据文档设计，这是元模型的核心组件，负责管理所有属性定义
 * 从AbstractProperties.xml等属性库文件加载属性定义，提供统一的属性查找服务
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Slf4j
public class GlobalPropertyPool {

    private static volatile GlobalPropertyPool instance;

    /**
     * 全局属性池：存储所有已加载的属性定义
     * Key: 属性ID, Value: 属性定义
     */
    private final Map<String, PropertyDefinition> globalProperties = new ConcurrentHashMap<>();

    /**
     * 按作用域分组的属性池
     * Key: 作用域(如PageModel, DataModel), Value: 该作用域下的属性列表
     */
    private final Map<String, List<PropertyDefinition>> propertiesByScope = new ConcurrentHashMap<>();

    private GlobalPropertyPool() {
        // 私有构造函数，单例模式
    }

    public static GlobalPropertyPool getInstance() {
        if (instance == null) {
            synchronized (GlobalPropertyPool.class) {
                if (instance == null) {
                    instance = new GlobalPropertyPool();
                }
            }
        }
        return instance;
    }

    /**
     * 注册属性定义到全局池
     */
    public void registerProperty(PropertyDefinition property) {
        if (property == null || property.getId() == null) {
            log.warn("尝试注册无效的属性定义");
            return;
        }

        // 检查是否已存在同ID的属性
        PropertyDefinition existing = globalProperties.get(property.getId());
        if (existing != null) {
            log.debug("属性 {} 已存在，将被覆盖", property.getId());
        }

        globalProperties.put(property.getId(), property);

        // 按作用域分组
        if (property.getScope() != null) {
            propertiesByScope.computeIfAbsent(property.getScope(), k -> new ArrayList<>()).add(property);
        }

        log.debug("注册属性: {} ({}), 作用域: {}", property.getId(), property.getName(), property.getScope());
    }

    /**
     * 批量注册属性定义
     */
    public void registerProperties(Collection<PropertyDefinition> properties) {
        if (properties != null) {
            properties.forEach(this::registerProperty);
        }
    }

    /**
     * 根据ID查找属性定义
     */
    public PropertyDefinition getProperty(String propertyId) {
        return globalProperties.get(propertyId);
    }

    /**
     * 根据ID列表批量查找属性定义
     */
    public List<PropertyDefinition> getProperties(List<String> propertyIds) {
        if (propertyIds == null) {
            return Collections.emptyList();
        }

        return propertyIds.stream()
                .map(this::getProperty)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 获取指定作用域下的所有属性
     */
    public List<PropertyDefinition> getPropertiesByScope(String scope) {
        return propertiesByScope.getOrDefault(scope, Collections.emptyList());
    }

    /**
     * 获取所有属性定义
     */
    public Collection<PropertyDefinition> getAllProperties() {
        return Collections.unmodifiableCollection(globalProperties.values());
    }

    /**
     * 检查属性是否存在
     */
    public boolean hasProperty(String propertyId) {
        return globalProperties.containsKey(propertyId);
    }

    /**
     * 验证属性引用列表是否都存在
     */
    public List<String> validatePropertyReferences(List<String> propertyIds) {
        if (propertyIds == null) {
            return Collections.emptyList();
        }

        return propertyIds.stream()
                .filter(id -> !hasProperty(id))
                .toList();
    }

    /**
     * 获取全局属性池统计信息
     */
    public PropertyPoolStats getStats() {
        PropertyPoolStats stats = new PropertyPoolStats();
        stats.totalProperties = globalProperties.size();
        stats.scopeCount = propertiesByScope.size();
        stats.propertiesByScope = new HashMap<>(propertiesByScope.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                )));
        return stats;
    }

    /**
     * 清空属性池（用于测试或重新加载）
     */
    public void clear() {
        globalProperties.clear();
        propertiesByScope.clear();
        log.info("全局属性池已清空");
    }

    /**
     * 属性池统计信息
     */
    public static class PropertyPoolStats {
        public int totalProperties;
        public int scopeCount;
        public Map<String, Integer> propertiesByScope;

        @Override
        public String toString() {
            return String.format("PropertyPoolStats{totalProperties=%d, scopeCount=%d, propertiesByScope=%s}",
                    totalProperties, scopeCount, propertiesByScope);
        }
    }
}
