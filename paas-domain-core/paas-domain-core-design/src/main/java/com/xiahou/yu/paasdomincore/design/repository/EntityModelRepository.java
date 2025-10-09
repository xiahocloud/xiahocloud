package com.xiahou.yu.paasdomincore.design.repository;

import com.xiahou.yu.paasdomincore.design.metadatamodel.EntityModel;
import org.springframework.stereotype.Repository;

/**
 * 数据模型 Repository 接口
 * 提供数据模型的数据访问操作
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Repository
public interface EntityModelRepository extends BaseRepository<EntityModel, Long> {

    /**
     * 根据类型查找数据模型
     *
     * @param type 模型类型
     * @return 数据模型列表
     */
    java.util.List<EntityModel> findByType(String type);

    /**
     * 根据命名空间查找数据模型
     *
     * @param namespace 命名空间
     * @return 数据模型列表
     */
    java.util.List<EntityModel> findByNamespace(String namespace);

    /**
     * 根据应用标识查找数据模型
     *
     * @param app 应用标识
     * @return 数据模型列表
     */
    java.util.List<EntityModel> findByApp(String app);

    /**
     * 检查租户内key是否存在
     *
     * @param tenant 租户编码
     * @param key 业务标识
     * @return 是否存在
     */
    boolean existsByTenantAndKey(String tenant, String key);
}
