package com.xiahou.yu.paasdomincore.design.service.impl;

import com.xiahou.yu.paasdomincore.design.metamodel.DataModel;
import com.xiahou.yu.paasdomincore.design.repository.DataModelRepository;
import com.xiahou.yu.paasdomincore.design.service.DataModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 数据模型服务实现类
 * 提供数据模型的业务操作实现
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataModelServiceImpl implements DataModelService {

    private final DataModelRepository dataModelRepository;

    @Override
    @Transactional
    public DataModel save(DataModel dataModel) {
        log.debug("保存数据模型: {}", dataModel);

        // 验证数据模型
        if (!validateDataModel(dataModel)) {
            throw new IllegalArgumentException("数据模型验证失败");
        }

        return dataModelRepository.save(dataModel);
    }

    @Override
    public Optional<DataModel> findById(Long id) {
        log.debug("根据ID查找数据模型: {}", id);
        return dataModelRepository.findById(id);
    }

    @Override
    public Optional<DataModel> findByTenantAndKey(String tenant, String key) {
        log.debug("根据租户和key查找数据模型: tenant={}, key={}", tenant, key);
        return dataModelRepository.findByTenantAndKey(tenant, key);
    }

    @Override
    public List<DataModel> findByTenant(String tenant) {
        log.debug("根据租户查找所有数据模型: {}", tenant);
        return dataModelRepository.findByTenant(tenant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("删除数据模型: {}", id);
        dataModelRepository.deleteById(id);
    }

    @Override
    public boolean exists(String tenant, String key) {
        log.debug("检查数据模型是否存在: tenant={}, key={}", tenant, key);
        return dataModelRepository.existsByTenantAndKey(tenant, key);
    }

    @Override
    public boolean validateDataModel(DataModel dataModel) {
        if (dataModel == null) {
            log.warn("数据模型不能为空");
            return false;
        }

        if (!StringUtils.hasText(dataModel.getTenant())) {
            log.warn("租户编码不能为空");
            return false;
        }

        if (!StringUtils.hasText(dataModel.getKey())) {
            log.warn("业务标识不能为空");
            return false;
        }

        if (!StringUtils.hasText(dataModel.getName())) {
            log.warn("名称不能为空");
            return false;
        }
        return true;
    }
}
