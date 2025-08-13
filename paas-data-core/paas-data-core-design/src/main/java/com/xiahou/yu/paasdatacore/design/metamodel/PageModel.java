package com.xiahou.yu.paasdatacore.design.metamodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

/**
 * 页面模型
 * 用于定义页面的结构和布局
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("t_page_model")
public class PageModel extends AbstractModel {

    /**
     * 页面类型
     * 如：list, form, detail等
     */
    @Column("page_type")
    private String pageType;

    /**
     * 布局方式
     * 页面的布局配置
     */
    @Column("layout")
    private String layout;

    /**
     * 模板
     * 页面使用的模板
     */
    @Column("template")
    private String template;

    /**
     * 页面路径
     * 访问页面的URL路径
     */
    @Column("page_path")
    private String pagePath;

    /**
     * 权限标识
     * 访问该页面需要的权限
     */
    @Column("permission")
    private String permission;

    /**
     * 是否启用
     * 标识页面是否可用
     */
    @Column("enabled")
    private Boolean enabled;
}
