package com.xiahou.yu.paaswebserver.controller;

import com.xiahou.yu.paaswebserver.dto.PageResponse;
import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.entity.IndustryIndexesDailyData;
import com.xiahou.yu.stockindicatoranalyzer.entity.IndustriesMasterInfo;
import com.xiahou.yu.stockindicatoranalyzer.service.IndustryIndexesDailyDataService;
import com.xiahou.yu.stockindicatoranalyzer.service.IndustriesMasterInfoService;
import com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis.IndustryDtoIndicatorAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/industries")
public class IndustryDataController {

    private final IndustriesMasterInfoService masterInfoService;
    private final IndustryIndexesDailyDataService indexesDailyDataService;
    private final IndustryDtoIndicatorAnalysisService analysisService;

    public IndustryDataController(IndustriesMasterInfoService masterInfoService,
                                  IndustryIndexesDailyDataService indexesDailyDataService,
                                  IndustryDtoIndicatorAnalysisService analysisService) {
        this.masterInfoService = masterInfoService;
        this.indexesDailyDataService = indexesDailyDataService;
        this.analysisService = analysisService;
    }

    // -------------- 行业主数据 --------------
    /**
     * 行业主数据分页列表（可选状态、层级筛选）
     */
    @GetMapping("/master")
    public PageResponse<IndustriesMasterInfo> listMaster(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "level", required = false) String level
    ) {
        List<IndustriesMasterInfo> items = masterInfoService.list(status, level, pageNum, pageSize);
        long total = masterInfoService.count(status, level);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 行业主数据详情（按行业代码）
     */
    @GetMapping("/master/{industryCode}")
    public IndustriesMasterInfo getMasterByCode(@PathVariable("industryCode") String industryCode) {
        return masterInfoService.findByCode(industryCode).orElse(null);
    }

    // -------------- 行业指数日线数据 --------------
    /**
     * 行业指数日线分页列表（所有）
     */
    @GetMapping("/indexes/daily")
    public PageResponse<IndustryIndexesDailyData> listDaily(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        List<IndustryIndexesDailyData> items = indexesDailyDataService.listAll(pageNum, pageSize);
        long total = indexesDailyDataService.countAll();
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指定行业指数代码的日线分页列表
     */
    @GetMapping("/indexes/daily/{indexCode}")
    public PageResponse<IndustryIndexesDailyData> listDailyByCode(
            @PathVariable("indexCode") String indexCode,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        List<IndustryIndexesDailyData> items = indexesDailyDataService.listByCode(indexCode, pageNum, pageSize);
        long total = indexesDailyDataService.countByCode(indexCode);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 指定行业指数代码在日期范围内的日线分页列表
     */
    @GetMapping("/indexes/daily/{indexCode}/range")
    public PageResponse<IndustryIndexesDailyData> listDailyByCodeAndRange(
            @PathVariable("indexCode") String indexCode,
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize
    ) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<IndustryIndexesDailyData> items = indexesDailyDataService.listByCodeAndDateRange(indexCode, startDate, endDate, pageNum, pageSize);
        long total = indexesDailyDataService.countByCodeAndDateRange(indexCode, startDate, endDate);
        return new PageResponse<>(pageNum, pageSize, total, items);
    }

    /**
     * 按行业名称的日线列表（升序，非分页）
     */
    @GetMapping("/indexes/daily/by-name")
    public List<IndustryIndexesDailyData> listByIndustryName(
            @RequestParam("industryName") String industryName
    ) {
        return indexesDailyDataService.listByIndustryName(industryName);
    }

    // -------------- DTO 指标增强 --------------
    /**
     * 指标增强（DTO）：按行业指数代码与日期范围返回已计算 SMA/EMA/MACD/BOLL 的统一 DTO 列表（升序）。
     */
    @GetMapping("/indexes/analysis/indicators/augment")
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