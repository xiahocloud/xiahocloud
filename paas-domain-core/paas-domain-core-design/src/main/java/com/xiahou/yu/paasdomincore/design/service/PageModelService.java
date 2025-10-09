package com.xiahou.yu.paasdomincore.design.service;

import com.xiahou.yu.paasdomincore.design.metadatamodel.PageModel;

import java.util.List;
import java.util.Optional;

/**
 * 页面模型服务接口
 * 提供页面模型的业务操作
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
public interface PageModelService {

    /**
     * 保存页面模型
     *
     * @param pageModel 页面模型
     * @return 保存后的页面模型
     */
    PageModel save(PageModel pageModel);

    /**
     * 根据ID查找页面模型
     *
     * @param id 主键ID
     * @return 页面模型
     */
    Optional<PageModel> findById(Long id);

    /**
     * 根据租户和key查找页面模型
     *
     * @param tenant 租户编码
     * @param key 业务标识
     * @return 页面模型
     */
    Optional<PageModel> findByTenantAndKey(String tenant, String key);

    /**
     * 根据租户查找所有页面模型
     *
     * @param tenant 租户编码
     * @return 页面模型列表
     */
    List<PageModel> findByTenant(String tenant);

    /**
     * 根据页面类型查找页面模型
     *
     * @param tenant 租户编码
     * @param pageType 页面类型
     * @return 页面模型列表
     */
    List<PageModel> findByPageType(String tenant, String pageType);

    /**
     * 根据页面路径查找页面模型
     *
     * @param tenant 租户编码
     * @param pagePath 页面路径
     * @return 页面模型
     */
    Optional<PageModel> findByPagePath(String tenant, String pagePath);

    /**
     * 查找启用的页面模型
     *
     * @param tenant 租户编码
     * @return 启用的页面模型列表
     */
    List<PageModel> findEnabledPages(String tenant);

    /**
     * 根据权限查找页面模型
     *
     * @param tenant 租户编码
     * @param permission 权限标识
     * @return 页面模型列表
     */
    List<PageModel> findByPermission(String tenant, String permission);

    /**
     * 删除页面模型
     *
     * @param id 主键ID
     */
    void deleteById(Long id);

    /**
     * 验证页面模型配置
     *
     * @param pageModel 页面模型
     * @return 验证结果
     */
    boolean validatePageModel(PageModel pageModel);

    /**
     * 启用或禁用页面
     *
     * @param id 页面ID
     * @param enabled 是否启用
     * @return 更新后的页面模型
     */
    PageModel updatePageStatus(Long id, Boolean enabled);
}
