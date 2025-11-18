package com.xiahou.yu.paaswebserver.init;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesMasterInfo;
import com.xiahou.yu.stockindicatoranalyzer.service.IndexesMasterInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 启动初始化：确保 indexes_master_info 表存在默认的“上证指数”元数据。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IndexMasterDataInitializer implements CommandLineRunner {

    private final IndexesMasterInfoService masterInfoService;

    @Override
    public void run(String... args) {
        try {
            // 默认插入：上证指数
            String defaultCode = "sh.000001"; // 与现有示例一致使用 sh. 前缀
            String defaultName = "上证指数";

            boolean existsByCode = false;
            boolean existsByName = false;
            try {
                existsByCode = masterInfoService.findByCode(defaultCode).isPresent();
            } catch (Exception e) {
                log.warn("IndexMasterDataInitializer: findByCode failed (table may not exist yet). Skipping init. Message={}", e.getMessage());
                return; // 表未创建，跳过初始化，避免阻塞服务启动
            }
            try {
                existsByName = masterInfoService.findByName(defaultName).isPresent();
            } catch (Exception e) {
                log.warn("IndexMasterDataInitializer: findByName failed (table may not exist yet). Skipping init. Message={}", e.getMessage());
                return; // 表未创建，跳过初始化，避免阻塞服务启动
            }

            if (!existsByCode && !existsByName) {
                IndexesMasterInfo info = IndexesMasterInfo.builder()
                        .indexCode(defaultCode)
                        .indexName(defaultName)
                        .shortName(defaultName)
                        .exchange("SSE")
                        .source("initializer")
                        .status("active")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                try {
                    masterInfoService.saveOrUpdate(info);
                } catch (Exception e) {
                    log.warn("IndexMasterDataInitializer: saveOrUpdate failed (table may not exist yet). Skipping init. Message={}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("IndexMasterDataInitializer skipped due to error: {}", e.getMessage());
        }
    }
}