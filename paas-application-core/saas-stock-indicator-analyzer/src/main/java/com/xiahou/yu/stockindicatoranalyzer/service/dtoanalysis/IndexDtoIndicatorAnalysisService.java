package com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.service.provider.IndexDailyLineProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class IndexDtoIndicatorAnalysisService extends BaseDtoIndicatorAnalysisService {

    private final IndexDailyLineProvider provider;

    public IndexDtoIndicatorAnalysisService(IndexDailyLineProvider provider) {
        this.provider = provider;
    }

    public List<DailyLineDTO> augmentByCodeBetween(String indexCode, LocalDate start, LocalDate end) {
        List<DailyLineDTO> list = provider.listByCodeBetween(indexCode, start, end);
        augmentIndicators(list);
        return list;
    }

    // 新增：支持自定义 BOLL 参数
    public List<DailyLineDTO> augmentByCodeBetween(String indexCode, LocalDate start, LocalDate end, int period, double k) {
        List<DailyLineDTO> list = provider.listByCodeBetween(indexCode, start, end);
        augmentIndicators(list, period, k);
        return list;
    }

    public List<DailyLineDTO> augmentLatest(String indexCode, int limit) {
        List<DailyLineDTO> list = provider.listLatestAsc(indexCode, limit);
        augmentIndicators(list);
        return list;
    }

    // 新增：最新数据支持自定义 BOLL 参数
    public List<DailyLineDTO> augmentLatest(String indexCode, int limit, int period, double k) {
        List<DailyLineDTO> list = provider.listLatestAsc(indexCode, limit);
        augmentIndicators(list, period, k);
        return list;
    }
}