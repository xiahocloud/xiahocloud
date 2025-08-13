package com.xiahou.yu.paasmetacore.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 模型定义
 * 对应 XML 中的 Model 标签
 *
 * @author wanghaoxin
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ModelDefinition {

    /**
     * 模型唯一标识，也是要生成的Java类名
     */
    private String id;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 导入的XML文件路径
     */
    private String importPath;

    /**
     * 继承的父模型ID
     */
    private String extendsModel;

    /**
     * 是否为抽象模型
     */
    private boolean isAbstract;

    /**
     * 模型中声明的所有属性
     * Key: 属性ID, Value: 属性定义
     */
    private Map<String, PropertyDefinition> declaredProperties;

    /**
     * 模型中声明的所有组件
     * Key: 组件ID, Value: 组件定义
     */
    private Map<String, ComponentDefinition> declaredComponents;

    /**
     * 模型实际引用的属性列表（对应Refs中的Ref）
     * 这些是要生成到Java类中的字段
     */
    private List<String> referencedProperties;

    /**
     * 模型实际引用的组件列表
     */
    private List<String> referencedComponents;

    /**
     * 获取数据库表名
     * 规则：t_{模型ID转snake_case}
     */
    public String getTableName() {
        if (isAbstract) {
            return null; // 抽象模型不创建表
        }
        return "t_" + camelToSnakeCase(id);
    }

    /**
     * 将驼峰命名转换为下划线命名
     */
    private String camelToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
