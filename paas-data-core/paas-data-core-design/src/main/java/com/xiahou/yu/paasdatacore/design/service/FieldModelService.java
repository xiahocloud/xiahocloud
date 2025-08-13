package com.xiahou.yu.paasdatacore.design.service;

import com.xiahou.yu.paasdatacore.design.metamodel.FieldModel;

import java.util.List;
import java.util.Optional;

/**
 * 字段模型服务接口
 * 提供字段模型的业务操作
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
public interface FieldModelService {

    /**
     * 保存字段模型
     *
     * @param fieldModel 字段模型
     * @return 保存后的字段模型
     */
    FieldModel save(FieldModel fieldModel);

    /**
     * 根据ID查找字段模型
     *
     * @param id 主键ID
     * @return 字段模型
     */
    Optional<FieldModel> findById(Long id);

    /**
     * 根据租户和key查找字段模型
     *
     * @param tenant 租户编码
     * @param key 业务标识
     * @return 字段模型
     */
    Optional<FieldModel> findByTenantAndKey(String tenant, String key);

    /**
     * 根据租户查找所有字段模型
     *
     * @param tenant 租户编码
     * @return 字段模型列表
     */
    List<FieldModel> findByTenant(String tenant);

    /**
     * 根据字段类型查找字段模型
     *
     * @param tenant 租户编码
     * @param fieldType 字段类型
     * @return 字段模型列表
     */
    List<FieldModel> findByFieldType(String tenant, String fieldType);

    /**
     * 查找必填字段
     *
     * @param tenant 租户编码
     * @return 必填字段列表
     */
    List<FieldModel> findRequiredFields(String tenant);

    /**
     * 根据列名查找字段模型
     *
     * @param tenant 租户编码
     * @param columnName 列名
     * @return 字段模型列表
     */
    List<FieldModel> findByColumnName(String tenant, String columnName);

    /**
     * 删除字段模型
     *
     * @param id 主键ID
     */
    void deleteById(Long id);

    /**
     * 验证字段模型配置
     *
     * @param fieldModel 字段模型
     * @return 验证结果
     */
    boolean validateFieldModel(FieldModel fieldModel);

    /**
     * 根据验证规则查找字段
     *
     * @param tenant 租户编码
     * @return 有验证规则的字段列表
     */
    List<FieldModel> findFieldsWithValidation(String tenant);
}
