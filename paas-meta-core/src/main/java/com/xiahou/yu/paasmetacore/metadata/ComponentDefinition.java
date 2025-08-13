package com.xiahou.yu.paasmetacore.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 组件定义
 * 对应 XML 中的 Component 标签
 * 支持组件继承和属性引用
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ComponentDefinition {

    /**
     * 组件唯一标识
     */
    private String id;

    /**
     * 组件名称
     */
    private String name;

    /**
     * 组件描述
     */
    private String description;

    /**
     * 组件类型
     */
    private String type;

    /**
     * 继承的父组件ID
     */
    private String extendsComponent;

    /**
     * 导入的属性库文件路径
     */
    private String importPath;

    /**
     * 组件内声明的属性定义
     * Key: 属性ID, Value: 属性定义
     */
    private Map<String, PropertyDefinition> declaredProperties;

    /**
     * 组件实际引用的属性列表（对应Refs中���Ref）
     * 这些属性ID需要在属性池中查找对应的PropertyDefinition
     */
    private List<String> referencedProperties;

    /**
     * 组件包含的子组件列表
     */
    private List<ComponentDefinition> subComponents;
}
