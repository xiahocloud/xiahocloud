package com.xiahou.yu.paasdomincore.design.metadatamodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * 字段模型
 * 用于定义数据字段的属性和行为，按照元模型配置生成
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("t_field_model")
public class FieldModel extends AbstractModel {

    /**
     * 宽度
     * 字段在界面上的显示宽度
     */
    @Column("width")
    private String width;

    /**
     * 高度
     * 字段在界面上的显示高度
     */
    @Column("height")
    private String height;

    /**
     * 列名
     * 对应数据库表的列名
     */
    @Column("column_name")
    private String columnName;

    /**
     * 列类型
     * 数据库列的数据类型
     */
    @Column("column_type")
    private String columnType;

    /**
     * 字段类型
     * 前端显示的控件类型
     */
    @Column("field_type")
    private String fieldType;

    /**
     * 是否必填
     * 标识该字段是否为必填项
     */
    @Column("required")
    private Boolean required;

    /**
     * 默认值
     * 字段的默认值
     */
    @Column("default_value")
    private String defaultValue;

    /**
     * 验证规则
     * 字段的验证规则表达式
     */
    @Column("validation_rule")
    private String validationRule;
}
