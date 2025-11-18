package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dto.PageResponse;
import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesMasterInfo;
import com.xiahou.yu.stockindicatoranalyzer.service.IndexesMasterInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/indexes/master")
public class IndexMasterController {

    private final IndexesMasterInfoService masterInfoService;

    public IndexMasterController(IndexesMasterInfoService masterInfoService) {
        this.masterInfoService = masterInfoService;
    }

    /**
     * 指数主数据分页列表
     */
    @GetMapping
    public PageResponse<IndexesMasterInfo> list(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        List<IndexesMasterInfo> items = masterInfoService.list(pageNum, pageSize);
        long total = masterInfoService.count();
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指数主数据详情（按指数代码）
     */
    @GetMapping("/{indexCode}")
    public IndexesMasterInfo getByCode(@PathVariable("indexCode") String indexCode) {
        return masterInfoService.findByCode(indexCode).orElse(null);
    }
}