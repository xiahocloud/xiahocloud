package com.xiahou.yu.paasdomincore.design.repository;

import com.xiahou.yu.paasdomincore.design.metadatamodel.FieldModel;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 字段模型仓储接口
 * 提供字段模型的持久化操作
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Repository
public interface FieldModelRepository extends BaseRepository<FieldModel, Long> {

    /**
     * 根据列名查找字段模型
     *
     * @param tenant 租户编码
     * @param columnName 列名
     * @return 字段模型列表
     */
    List<FieldModel> findByTenantAndColumnName(String tenant, String columnName);

    /**
     * 根据字段类型查找字段模型
     *
     * @param tenant 租户编码
     * @param fieldType 字段类型
     * @return 字段模型列表
     */
    List<FieldModel> findByTenantAndFieldType(String tenant, String fieldType);

    /**
     * 根据列类型查找字段模型
     *
     * @param tenant 租户编码
     * @param columnType 列类型
     * @return 字段模型列表
     */
    List<FieldModel> findByTenantAndColumnType(String tenant, String columnType);

    /**
     * 查找必填字段
     *
     * @param tenant 租户编码
     * @param required 是否必填
     * @return 字段模型列表
     */
    List<FieldModel> findByTenantAndRequired(String tenant, Boolean required);

    /**
     * 根据默认值查找字段模型
     *
     * @param tenant 租户编码
     * @param defaultValue 默认值
     * @return 字段模型列表
     */
    List<FieldModel> findByTenantAndDefaultValue(String tenant, String defaultValue);

    /**
     * 查找有验证规则的字段
     *
     * @param tenant 租户编码
     * @return 字段模型列表
     */
    @Query("SELECT * FROM t_field_model WHERE tenant = :tenant AND validation_rule IS NOT NULL AND validation_rule != ''")
    List<FieldModel> findFieldsWithValidationRule(@Param("tenant") String tenant);

    /**
     * 根据字段类型和是否必填查找
     *
     * @param tenant 租户编码
     * @param fieldType 字段类型
     * @param required 是否必填
     * @return 字段模型列表
     */
    List<FieldModel> findByTenantAndFieldTypeAndRequired(String tenant, String fieldType, Boolean required);

    /**
     * 根据列名和列类型查找唯一字段
     *
     * @param tenant 租户编码
     * @param columnName 列名
     * @param columnType 列类型
     * @return 字段模型
     */
    Optional<FieldModel> findByTenantAndColumnNameAndColumnType(String tenant, String columnName, String columnType);

    /**
     * 统计指定字段类型的数量
     *
     * @param tenant 租户编码
     * @param fieldType 字段类型
     * @return 字段数量
     */
    @Query("SELECT COUNT(*) FROM t_field_model WHERE tenant = :tenant AND field_type = :fieldType")
    long countByTenantAndFieldType(@Param("tenant") String tenant, @Param("fieldType") String fieldType);
}
