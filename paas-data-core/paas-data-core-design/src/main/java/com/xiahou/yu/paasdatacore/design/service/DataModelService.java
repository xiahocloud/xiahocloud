package com.xiahou.yu.paasdatacore.design.service;

import com.xiahou.yu.paasdatacore.design.metamodel.DataModel;

import java.util.List;
import java.util.Optional;

/**
 * 数据模型服务接口
 * 提供数据模型的业务操作
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
public interface DataModelService {

    /**
     * 保存数据模型
     *
     * @param dataModel 数据模型
     * @return 保存后的数据模型
     */
    DataModel save(DataModel dataModel);

    /**
     * 根据ID查找数据模型
     *
     * @param id 主键ID
     * @return 数据模型
     */
    Optional<DataModel> findById(Long id);

    /**
     * 根据租户和key查找数据模型
     *
     * @param tenant 租户编码
     * @param key 业务标识
     * @return 数据模型
     */
    Optional<DataModel> findByTenantAndKey(String tenant, String key);

    /**
     * 根据租户查找所有数据模型
     *
     * @param tenant 租户编码
     * @return 数据模型列表
     */
    List<DataModel> findByTenant(String tenant);

    /**
     * 删除数据模型
     *
     * @param id 主键ID
     */
    void deleteById(Long id);

    /**
     * 检查数据模型是否存在
     *
     * @param tenant 租户编码
     * @param key 业务标识
     * @return 是否存在
     */
    boolean exists(String tenant, String key);

    /**
     * 验证数据模型配置
     *
     * @param dataModel 数据模型
     * @return 验证结果
     */
    boolean validateDataModel(DataModel dataModel);
}
