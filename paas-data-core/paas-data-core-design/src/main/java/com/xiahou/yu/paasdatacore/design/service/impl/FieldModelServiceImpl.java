package com.xiahou.yu.paasdatacore.design.service.impl;

import com.xiahou.yu.paasdatacore.design.metamodel.FieldModel;
import com.xiahou.yu.paasdatacore.design.repository.FieldModelRepository;
import com.xiahou.yu.paasdatacore.design.service.FieldModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 字段模型服务实现类
 * 提供字段模型的业务操作实现
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldModelServiceImpl implements FieldModelService {

    private final FieldModelRepository fieldModelRepository;

    @Override
    @Transactional
    public FieldModel save(FieldModel fieldModel) {
        log.debug("保存字段模型: {}", fieldModel);

        // 验证字段模型
        if (!validateFieldModel(fieldModel)) {
            throw new IllegalArgumentException("字段模型验证失败");
        }

        return fieldModelRepository.save(fieldModel);
    }

    @Override
    public Optional<FieldModel> findById(Long id) {
        log.debug("根据ID查找字段模型: {}", id);
        return fieldModelRepository.findById(id);
    }

    @Override
    public Optional<FieldModel> findByTenantAndKey(String tenant, String key) {
        log.debug("根据租户和key查找字段模型: tenant={}, key={}", tenant, key);
        return fieldModelRepository.findByTenantAndKey(tenant, key);
    }

    @Override
    public List<FieldModel> findByTenant(String tenant) {
        log.debug("根据租户查找所有字段模型: {}", tenant);
        return fieldModelRepository.findByTenant(tenant);
    }

    @Override
    public List<FieldModel> findByFieldType(String tenant, String fieldType) {
        log.debug("根据字段类型查找字段模型: tenant={}, fieldType={}", tenant, fieldType);
        return fieldModelRepository.findByTenantAndFieldType(tenant, fieldType);
    }

    @Override
    public List<FieldModel> findRequiredFields(String tenant) {
        log.debug("查找必填字段: {}", tenant);
        return fieldModelRepository.findByTenantAndRequired(tenant, true);
    }

    @Override
    public List<FieldModel> findByColumnName(String tenant, String columnName) {
        log.debug("根据列名查找字段模型: tenant={}, columnName={}", tenant, columnName);
        return fieldModelRepository.findByTenantAndColumnName(tenant, columnName);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("删除字段模型: {}", id);
        fieldModelRepository.deleteById(id);
    }

    @Override
    public boolean validateFieldModel(FieldModel fieldModel) {
        if (fieldModel == null) {
            log.warn("字段模型不能为空");
            return false;
        }

        if (!StringUtils.hasText(fieldModel.getTenant())) {
            log.warn("租户编码不能为空");
            return false;
        }

        if (!StringUtils.hasText(fieldModel.getKey())) {
            log.warn("业务标识不能为空");
            return false;
        }

        if (!StringUtils.hasText(fieldModel.getName())) {
            log.warn("名称不能为空");
            return false;
        }

        if (!StringUtils.hasText(fieldModel.getColumnName())) {
            log.warn("列名不能为空");
            return false;
        }

        if (!StringUtils.hasText(fieldModel.getFieldType())) {
            log.warn("字段类型不能为空");
            return false;
        }

        return true;
    }

    @Override
    public List<FieldModel> findFieldsWithValidation(String tenant) {
        log.debug("查找有验证规则的字段: {}", tenant);
        return fieldModelRepository.findFieldsWithValidationRule(tenant);
    }
}
