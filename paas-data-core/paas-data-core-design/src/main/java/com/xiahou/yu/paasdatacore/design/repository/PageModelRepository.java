package com.xiahou.yu.paasdatacore.design.repository;

import com.xiahou.yu.paasdatacore.design.metamodel.PageModel;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 页面模型仓储接口
 * 提供页面模型的持久化操作
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Repository
public interface PageModelRepository extends BaseRepository<PageModel, Long> {

    /**
     * 根据页面类型查找页面模型
     *
     * @param tenant 租户编码
     * @param pageType 页面类型
     * @return 页面模型列表
     */
    List<PageModel> findByTenantAndPageType(String tenant, String pageType);

    /**
     * 根据页面路径查找页面模型
     *
     * @param tenant 租户编码
     * @param pagePath 页面路径
     * @return 页面模型
     */
    Optional<PageModel> findByTenantAndPagePath(String tenant, String pagePath);

    /**
     * 根据模板查找页面模型
     *
     * @param tenant 租户编码
     * @param template 模板
     * @return 页面模型列表
     */
    List<PageModel> findByTenantAndTemplate(String tenant, String template);

    /**
     * 根据权限标识查找页面模型
     *
     * @param tenant 租户编码
     * @param permission 权限标识
     * @return 页面模型列表
     */
    List<PageModel> findByTenantAndPermission(String tenant, String permission);

    /**
     * 查找启用的页面模型
     *
     * @param tenant 租户编码
     * @param enabled 是否启用
     * @return 页面模型列表
     */
    List<PageModel> findByTenantAndEnabled(String tenant, Boolean enabled);

    /**
     * 根据布局方式查找页面模型
     *
     * @param tenant 租户编码
     * @param layout 布局方式
     * @return 页面模型列表
     */
    List<PageModel> findByTenantAndLayout(String tenant, String layout);

    /**
     * 根据页面类型和是否启用查找
     *
     * @param tenant 租户编码
     * @param pageType 页面类型
     * @param enabled 是否启用
     * @return 页面模型列表
     */
    List<PageModel> findByTenantAndPageTypeAndEnabled(String tenant, String pageType, Boolean enabled);

    /**
     * 查找指定权限下的启用页面
     *
     * @param tenant 租户编码
     * @param permission 权限标识
     * @param enabled 是否启用
     * @return 页面模型列表
     */
    List<PageModel> findByTenantAndPermissionAndEnabled(String tenant, String permission, Boolean enabled);

    /**
     * 自定义查询：根据页面路径前缀查找
     *
     * @param tenant 租户编码
     * @param pathPrefix 路径前缀
     * @return 页面模型列表
     */
    @Query("SELECT * FROM t_page_model WHERE tenant = :tenant AND page_path LIKE :pathPrefix%")
    List<PageModel> findByPagePathPrefix(@Param("tenant") String tenant, @Param("pathPrefix") String pathPrefix);

    /**
     * 统计指定页面类型的数量
     *
     * @param tenant 租户编码
     * @param pageType 页面类型
     * @return 页面数量
     */
    @Query("SELECT COUNT(*) FROM t_page_model WHERE tenant = :tenant AND page_type = :pageType")
    long countByTenantAndPageType(@Param("tenant") String tenant, @Param("pageType") String pageType);
}
