package com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.service.provider.StockDailyLineProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockDtoAnalysisService extends BaseDtoIndicatorAnalysisService {

    private final StockDailyLineProvider provider;

    public StockDtoAnalysisService(StockDailyLineProvider provider) {
        this.provider = provider;
    }

    public List<DailyLineDTO> augmentByCodeBetween(String stockCode, LocalDate start, LocalDate end) {
        List<DailyLineDTO> list = provider.listByCodeBetween(stockCode, start, end);
        augmentIndicators(list);
        return list;
    }

    // 新增：支持自定义 BOLL 参数
    public List<DailyLineDTO> augmentByCodeBetween(String stockCode, LocalDate start, LocalDate end, int period, double k) {
        List<DailyLineDTO> list = provider.listByCodeBetween(stockCode, start, end);
        augmentIndicators(list, period, k);
        return list;
    }

    public List<DailyLineDTO> augmentLatest(String stockCode, int limit) {
        List<DailyLineDTO> list = provider.listLatestAsc(stockCode, limit);
        augmentIndicators(list);
        return list;
    }

    // 新增：最新数据支持自定义 BOLL 参数
    public List<DailyLineDTO> augmentLatest(String stockCode, int limit, int period, double k) {
        List<DailyLineDTO> list = provider.listLatestAsc(stockCode, limit);
        augmentIndicators(list, period, k);
        return list;
    }
}