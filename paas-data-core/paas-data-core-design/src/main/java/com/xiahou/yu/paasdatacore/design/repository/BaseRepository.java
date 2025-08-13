package com.xiahou.yu.paasdatacore.design.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * 基础仓储接口
 * 提供通用的CRUD操作方法
 *
 * @param <T> 实体类型
 * @param <ID> 主键类型
 * @author paas-data-core-design
 * @version 0.0.1
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends CrudRepository<T, ID> {

    /**
     * 根据租户和key查找实体
     *
     * @param tenant 租户编码
     * @param key 业务标识
     * @return 实体对象
     */
    Optional<T> findByTenantAndKey(String tenant, String key);

    /**
     * 根据租户查找所有实体
     *
     * @param tenant 租户编码
     * @return 实体列表
     */
    List<T> findByTenant(String tenant);

    /**
     * 根据租户和名称查找实体
     *
     * @param tenant 租户编码
     * @param name 名称
     * @return 实体列表
     */
    List<T> findByTenantAndNameContaining(String tenant, String name);

    /**
     * 检查在指定租户下key是否存在
     *
     * @param tenant 租户编码
     * @param key 业务标识
     * @return 是否存在
     */
    boolean existsByTenantAndKey(String tenant, String key);

    /**
     * 根据租户删除实体
     *
     * @param tenant 租户编码
     * @return 删除的记录数
     */
    long deleteByTenant(String tenant);
}
