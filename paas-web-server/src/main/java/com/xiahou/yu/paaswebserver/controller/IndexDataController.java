package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dto.PageResponse;
import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesDailyData;
import com.xiahou.yu.stockindicatoranalyzer.service.IndexesDailyDataService;
import com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis.IndexDtoIndicatorAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/indexes")
public class IndexDataController {

    private final IndexesDailyDataService indexesDailyDataService;
    private final IndexDtoIndicatorAnalysisService analysisService;

    public IndexDataController(IndexesDailyDataService indexesDailyDataService,
                               IndexDtoIndicatorAnalysisService analysisService) {
        this.indexesDailyDataService = indexesDailyDataService;
        this.analysisService = analysisService;
    }

    /**
     * 指数日线分页列表（所有）
     */
    @GetMapping("/daily")
    public PageResponse<IndexesDailyData> listDaily(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        List<IndexesDailyData> items = indexesDailyDataService.listAll(pageNum, pageSize);
        long total = indexesDailyDataService.countAll();
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指定指数代码的日线分页列表
     */
    @GetMapping("/daily/{indexCode}")
    public PageResponse<IndexesDailyData> listDailyByCode(
            @PathVariable("indexCode") String indexCode,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        List<IndexesDailyData> items = indexesDailyDataService.listByCode(indexCode, pageNum, pageSize);
        long total = indexesDailyDataService.countByCode(indexCode);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指定指数代码在日期范围内的日线分页列表
     */
    @GetMapping("/daily/{indexCode}/range")
    public PageResponse<IndexesDailyData> listDailyByCodeAndRange(
            @PathVariable("indexCode") String indexCode,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<IndexesDailyData> items = indexesDailyDataService.listByCodeAndDateRange(indexCode, startDate, endDate, pageNum, pageSize);
        long total = indexesDailyDataService.countByCodeAndDateRange(indexCode, startDate, endDate);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指标增强（DTO）：按指数代码与日期范围返回已计算 SMA/EMA/MACD/BOLL 的统一 DTO 列表（升序）。
     */
    @GetMapping("/analysis/indicators/augment")
    public List<DailyLineDTO> augmentIndicators(
            @RequestParam("indexCode") String indexCode,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "period", required = false, defaultValue = "20") int period,
            @RequestParam(value = "k", required = false, defaultValue = "2.0") double k
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<DailyLineDTO> list = analysisService.augmentByCodeBetween(indexCode, startDate, endDate, period, k);
        return list;
    }
}