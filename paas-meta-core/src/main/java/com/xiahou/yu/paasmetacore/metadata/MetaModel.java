package com.xiahou.yu.paasmetacore.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 元模型定义
 * 对应 Metamodel.xml 中的 MetaModel 标签
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MetaModel {

    /**
     * 版本号
     */
    private String version;

    /**
     * 所有模型定义
     * Key: 模型ID, Value: 模型定义
     */
    private Map<String, ModelDefinition> models;

    /**
     * 所有组件定义
     * Key: 组件ID, Value: 组件定义
     */
    private Map<String, ComponentDefinition> components;

    /**
     * 所有属性定义
     * Key: 属性ID, Value: 属性定义
     */
    private Map<String, PropertyDefinition> properties;
}
