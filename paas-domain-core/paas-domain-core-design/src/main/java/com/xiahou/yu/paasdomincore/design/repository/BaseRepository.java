package com.xiahou.yu.paasdomincore.design.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * 基础 Repository 接口 - Spring Data JDBC 版本
 * 所有数据访问层都应该继承此接口
 *
 * @param <T> 实体类型
 * @param <ID> 主键类型
 * @author xiahou
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

    /**
     * 根据租户和业务键查找实体
     *
     * @param tenant 租户编码
     * @param key 业务键
     * @return 实体对象
     */
    @Query("SELECT * FROM data_model WHERE tenant = :tenant AND key = :key")
    T findByTenantAndKey(String tenant, String key);

    /**
     * 检查租户内业务键是否存在
     *
     * @param tenant 租户编码
     * @param key 业务键
     * @return 是否存在
     */
    @Query("SELECT COUNT(*) > 0 FROM data_model WHERE tenant = :tenant AND key = :key")
    boolean existsByTenantAndKey(String tenant, String key);

    /**
     * 根据租户查找所有实体
     *
     * @param tenant 租户编码
     * @return 实体列表
     */
    @Query("SELECT * FROM data_model WHERE tenant = :tenant")
    Iterable<T> findByTenant(String tenant);
}
