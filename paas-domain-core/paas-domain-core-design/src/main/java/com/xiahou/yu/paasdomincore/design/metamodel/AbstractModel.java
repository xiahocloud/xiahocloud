package com.xiahou.yu.paasdomincore.design.metamodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

/**
 * 抽象模型
 * 用于定义模型的通用部分，基于元模型配置生成
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractModel implements Persistable<Long> {

    /**
     * 全局唯一标识符
     * 技术上的唯一属性
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * 数据代码
     * 租户内的唯一属性
     */
    @Column("code")
    private String code;

    /**
     * 标识
     * 租户内可见的唯一属性
     */
    @Column("ukey")
    private String key;

    /**
     * 租户编码
     * 租户唯一编码
     */
    @Column("tenant")
    private String tenant;

    /**
     * 名称
     * 中文名称
     */
    @Column("name")
    private String name;

    /**
     * 描述
     * 关于属性的描述
     */
    @Column("description")
    private String description;

    /**
     * 版本
     * 版本号
     */
    @Column("version")
    private String version;

    /**
     * 类型
     * 描述当前模型的类型
     */
    @Column("type")
    private String type;

    /**
     * 状态
     * 描述元素的状态
     */
    @Column("status")
    private Integer status;

    /**
     * 启用
     * 启用状态， null / 1：启用， 0 ：禁用
     */
    @Column("enable")
    private Integer enable;

    /**
     * 可见
     * 可见性， null / 1：可见， 0 ：不可见
     */
    @Column("visible")
    private Integer visible;

    /**
     * 应用标识
     * 所属应用的标识
     */
    @Column("app")
    private String app;

    /**
     * 系统标识
     * 可见性， null / 1：系统 0 ：自定义
     */
    @Column("sys")
    private Integer sys;

    @Column("namespace")
    private String namespace;

    /**
     * 创建时间
     * 记录创建的时间戳
     */
    @Column("created_time")
    private LocalDateTime createdTime;

    /**
     * 创建人
     * 记录创建者的标识
     */
    @Column("created_by")
    private String createdBy;

    /**
     * 更新时间
     * 记录最后更新的时间戳
     */
    @Column("updated_time")
    private LocalDateTime updatedTime;

    /**
     * 更新人
     * 记录最后更新者的标识
     */
    @Column("updated_by")
    private String updatedBy;

    /**
     * 标记实体是否为新实体（用于Spring Data JDBC判断插入或更新）
     */
    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * 设置实体为已存在（通常在保存后调用）
     */
    public void markAsNotNew() {
        this.isNew = false;
    }

}
