package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.entity.IndustryIndexesDailyData;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksInfoMasterRepository;
import com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis.IndustryDtoIndicatorAnalysisService;
import com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis.StockDtoAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 股票筛选服务：
 * 1）股票所属行业处于“增长阶段”（算法化判断）；
 * 2）股票自身 EXPMA、MACD、BOLL 均处于较优阶段。
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockScreeningService {

    private final StocksInfoMasterRepository infoRepo;
    private final IndustryIndexesDailyDataService industryDailyService;
    private final IndustryDtoIndicatorAnalysisService industryDtoAnalysisService;
    private final StockDtoAnalysisService stockDtoAnalysisService;

    /**
     * 主筛选方法。
     * @param lookbackDays 股票指标计算所需的最近天数
     * @param industryLookbackDays 行业指标计算所需的最近天数
     * @param withinDays MACD 交叉“最近发生”的容忍天数（例如 7 天内）
     * @param bollPeriod 布林带均线周期
     * @param bollK 布林带标准差倍数
     * @param emaSlopeDays EMA20 上升斜率判断所需的最近天数窗口
     * @return 满足条件的股票代码列表
     */
    public List<String> screenGrowthAndIndicators(
            int lookbackDays,
            int industryLookbackDays,
            int withinDays,
            int bollPeriod,
            double bollK,
            int emaSlopeDays
    ) {
        Iterable<StocksInfoMaster> allInfos = infoRepo.findAll();
        List<String> passed = new ArrayList<>();

        for (StocksInfoMaster info : allInfos) {
            String stockCode = info.getStockCode();
            String industryName = info.getIndustryCategory();
            if (stockCode == null || stockCode.isEmpty()) continue;
            if (industryName == null || industryName.isEmpty()) continue;

            // 1) 获取行业指数代码（通过行业名称找到最近的日线记录，取其 indexCode）
            List<IndustryIndexesDailyData> industryList = industryDailyService.listByIndustryName(industryName);
            if (industryList == null || industryList.isEmpty()) continue;
            IndustryIndexesDailyData latestIndustry = industryList.stream()
                    .max(Comparator.comparing(IndustryIndexesDailyData::getTradeDate))
                    .orElse(null);
            if (latestIndustry == null || latestIndustry.getIndexCode() == null) continue;
            String indexCode = latestIndustry.getIndexCode();

            // 2) 行业是否处于增长阶段
            List<DailyLineDTO> industryDtos = industryDtoAnalysisService.augmentLatest(indexCode, Math.max(industryLookbackDays, 60), bollPeriod, bollK);
            if (!isIndustryGrowthPhase(industryDtos, emaSlopeDays, withinDays)) {
                continue;
            }

            // 3) 股票自身是否处于较优指标阶段
            List<DailyLineDTO> stockDtos = stockDtoAnalysisService.augmentLatest(stockCode, Math.max(lookbackDays, 60), bollPeriod, bollK);
            if (!isStockFavorablePhase(stockDtos, emaSlopeDays, withinDays)) {
                continue;
            }

            passed.add(stockCode);
        }

        return passed;
    }

    /**
     * 支持过滤与分页的筛选方法，用于加速烟测/分批筛选。
     */
    public List<String> screenGrowthAndIndicatorsFiltered(
            String exchange,
            String assetType,
            String status,
            int limit,
            int offset,
            int lookbackDays,
            int industryLookbackDays,
            int withinDays,
            int bollPeriod,
            double bollK,
            int emaSlopeDays
    ) {
        List<StocksInfoMaster> infos = infoRepo.listWithFilters(exchange, assetType, status, Math.max(limit, 1), Math.max(offset, 0));
        List<String> passed = new ArrayList<>();
        for (StocksInfoMaster info : infos) {
            String stockCode = info.getStockCode();
            String industryName = info.getIndustryCategory();
            if (stockCode == null || stockCode.isEmpty()) continue;
            if (industryName == null || industryName.isEmpty()) continue;

            List<IndustryIndexesDailyData> industryList = industryDailyService.listByIndustryName(industryName);
            if (industryList == null || industryList.isEmpty()) continue;
            IndustryIndexesDailyData latestIndustry = industryList.stream()
                    .max(Comparator.comparing(IndustryIndexesDailyData::getTradeDate))
                    .orElse(null);
            if (latestIndustry == null || latestIndustry.getIndexCode() == null) continue;
            String indexCode = latestIndustry.getIndexCode();

            List<DailyLineDTO> industryDtos = industryDtoAnalysisService.augmentLatest(indexCode, Math.max(industryLookbackDays, 60), bollPeriod, bollK);
            if (!isIndustryGrowthPhase(industryDtos, emaSlopeDays, withinDays)) {
                continue;
            }

            List<DailyLineDTO> stockDtos = stockDtoAnalysisService.augmentLatest(stockCode, Math.max(lookbackDays, 60), bollPeriod, bollK);
            if (!isStockFavorablePhase(stockDtos, emaSlopeDays, withinDays)) {
                continue;
            }

            passed.add(stockCode);
        }
        return passed;
    }

    /** 行业“增长阶段”简化判定 */
    private boolean isIndustryGrowthPhase(List<DailyLineDTO> list, int emaSlopeDays, int withinDays) {
        if (list == null || list.size() < Math.max(emaSlopeDays + 1, 5)) return false;
        int last = list.size() - 1;
        DailyLineDTO d = list.get(last);
        // EMA 多头排列
        if (!gt(d.getEma5(), d.getEma10())) return false;
        if (!gt(d.getEma10(), d.getEma20())) return false;
        // EMA20 近几日上升
        if (!isSlopeUp(list, emaSlopeDays, (DailyLineDTO x) -> x.getEma20())) return false;
        // MACD 多头（dif >= dea，柱子 >= 0），或者最近 withinDays 金叉
        if (!macdBullOrRecentGolden(list, withinDays)) return false;
        // BOLL 中轨之上，且中轨近几日上升
        if (!ge(d.getClose(), d.getBbMiddle())) return false;
        if (!isSlopeUp(list, emaSlopeDays, (DailyLineDTO x) -> x.getBbMiddle())) return false;
        return true;
    }

    /** 股票“较优阶段”简化判定 */
    private boolean isStockFavorablePhase(List<DailyLineDTO> list, int emaSlopeDays, int withinDays) {
        if (list == null || list.size() < Math.max(emaSlopeDays + 1, 5)) return false;
        int last = list.size() - 1;
        DailyLineDTO d = list.get(last);
        // EXPMA 多头排列
        if (!gt(d.getEma5(), d.getEma10())) return false;
        if (!gt(d.getEma10(), d.getEma20())) return false;
        // EMA20 近几日上升
        if (!isSlopeUp(list, emaSlopeDays, (DailyLineDTO x) -> x.getEma20())) return false;
        // MACD 多头或最近金叉
        if (!macdBullOrRecentGolden(list, withinDays)) return false;
        // 布林中轨之上
        if (!ge(d.getClose(), d.getBbMiddle())) return false;
        return true;
    }

    private boolean macdBullOrRecentGolden(List<DailyLineDTO> list, int withinDays) {
        int n = list.size();
        int last = n - 1;
        DailyLineDTO d = list.get(last);
        if (d.getMacdDif() == null || d.getMacdDea() == null || d.getMacdHist() == null) return false;
        boolean bullNow = d.getMacdDif().doubleValue() >= d.getMacdDea().doubleValue() && d.getMacdHist().doubleValue() >= 0.0;
        if (bullNow) return true;
        // 检查最近 withinDays 是否发生金叉
        LocalDate now = LocalDate.now();
        LocalDate lastGoldenDate = null;
        for (int i = 1; i < n; i++) {
            BigDecimal difPrev = list.get(i - 1).getMacdDif();
            BigDecimal deaPrev = list.get(i - 1).getMacdDea();
            BigDecimal difNow = list.get(i).getMacdDif();
            BigDecimal deaNow = list.get(i).getMacdDea();
            if (difPrev == null || deaPrev == null || difNow == null || deaNow == null) continue;
            double sPrev = difPrev.doubleValue() - deaPrev.doubleValue();
            double sNow = difNow.doubleValue() - deaNow.doubleValue();
            if (sPrev <= 0.0 && sNow > 0.0) {
                lastGoldenDate = list.get(i).getTradeDate();
            }
        }
        if (lastGoldenDate == null) return false;
        long days = Math.abs(ChronoUnit.DAYS.between(lastGoldenDate, now));
        return withinDays <= 0 || days <= withinDays;
    }

    private boolean isSlopeUp(List<DailyLineDTO> list, int slopeDays, java.util.function.Function<DailyLineDTO, BigDecimal> getter) {
        int n = list.size();
        if (n < slopeDays + 1) return false;
        int start = Math.max(1, n - slopeDays);
        double sum = 0.0;
        for (int i = start; i < n; i++) {
            BigDecimal prev = getter.apply(list.get(i - 1));
            BigDecimal now = getter.apply(list.get(i));
            if (prev == null || now == null) return false;
            sum += now.doubleValue() - prev.doubleValue();
        }
        return sum > 0.0;
    }

    private boolean gt(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return false;
        return a.doubleValue() > b.doubleValue();
    }

    private boolean ge(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return false;
        return a.doubleValue() >= b.doubleValue();
    }
}