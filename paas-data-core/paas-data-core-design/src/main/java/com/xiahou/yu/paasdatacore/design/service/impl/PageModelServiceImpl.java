package com.xiahou.yu.paasdatacore.design.service.impl;

import com.xiahou.yu.paasdatacore.design.metamodel.PageModel;
import com.xiahou.yu.paasdatacore.design.repository.PageModelRepository;
import com.xiahou.yu.paasdatacore.design.service.PageModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 页面模型服务实现类
 * 提供页面模型的业务操作实现
 *
 * @author paas-data-core-design
 * @version 0.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageModelServiceImpl implements PageModelService {

    private final PageModelRepository pageModelRepository;

    @Override
    @Transactional
    public PageModel save(PageModel pageModel) {
        log.debug("保存页面模型: {}", pageModel);

        // 验证页面模型
        if (!validatePageModel(pageModel)) {
            throw new IllegalArgumentException("页面模型验证失败");
        }

        return pageModelRepository.save(pageModel);
    }

    @Override
    public Optional<PageModel> findById(Long id) {
        log.debug("根据ID查找页面模型: {}", id);
        return pageModelRepository.findById(id);
    }

    @Override
    public Optional<PageModel> findByTenantAndKey(String tenant, String key) {
        log.debug("根据租户和key查找页面模型: tenant={}, key={}", tenant, key);
        return pageModelRepository.findByTenantAndKey(tenant, key);
    }

    @Override
    public List<PageModel> findByTenant(String tenant) {
        log.debug("根据租户查找所有页面模型: {}", tenant);
        return pageModelRepository.findByTenant(tenant);
    }

    @Override
    public List<PageModel> findByPageType(String tenant, String pageType) {
        log.debug("根据页面类型查找页面模型: tenant={}, pageType={}", tenant, pageType);
        return pageModelRepository.findByTenantAndPageType(tenant, pageType);
    }

    @Override
    public Optional<PageModel> findByPagePath(String tenant, String pagePath) {
        log.debug("根据页面路径查找页面模型: tenant={}, pagePath={}", tenant, pagePath);
        return pageModelRepository.findByTenantAndPagePath(tenant, pagePath);
    }

    @Override
    public List<PageModel> findEnabledPages(String tenant) {
        log.debug("查找启用的页面模型: {}", tenant);
        return pageModelRepository.findByTenantAndEnabled(tenant, true);
    }

    @Override
    public List<PageModel> findByPermission(String tenant, String permission) {
        log.debug("根据权限查找页面模型: tenant={}, permission={}", tenant, permission);
        return pageModelRepository.findByTenantAndPermission(tenant, permission);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("删除页面模型: {}", id);
        pageModelRepository.deleteById(id);
    }

    @Override
    public boolean validatePageModel(PageModel pageModel) {
        if (pageModel == null) {
            log.warn("页面模型不能为空");
            return false;
        }

        if (!StringUtils.hasText(pageModel.getTenant())) {
            log.warn("租户编码不能为空");
            return false;
        }

        if (!StringUtils.hasText(pageModel.getKey())) {
            log.warn("业务标识不能为空");
            return false;
        }

        if (!StringUtils.hasText(pageModel.getName())) {
            log.warn("名称不能为空");
            return false;
        }

        if (!StringUtils.hasText(pageModel.getPageType())) {
            log.warn("页面类型不能为空");
            return false;
        }

        if (!StringUtils.hasText(pageModel.getPagePath())) {
            log.warn("页面路径不能为空");
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public PageModel updatePageStatus(Long id, Boolean enabled) {
        log.debug("更新页面状态: id={}, enabled={}", id, enabled);

        Optional<PageModel> pageModelOpt = pageModelRepository.findById(id);
        if (pageModelOpt.isEmpty()) {
            throw new IllegalArgumentException("页面模型不存在: " + id);
        }

        PageModel pageModel = pageModelOpt.get();
        pageModel.setEnabled(enabled);
        return pageModelRepository.save(pageModel);
    }
}
