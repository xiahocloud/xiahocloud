package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.dto.BollingerNarrowResult;
import com.xiahou.yu.stockindicatoranalyzer.dto.BollingerUpperChannelResult;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksDailyDataRepository;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksInfoMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BollingerAnalysisService {

    private final StocksInfoMasterRepository infoRepo;
    private final StocksDailyDataRepository dailyRepo;


    /**
     * 查找在最近 lookbackDays 天中出现布林线窄通道的股票。
     * period: 布林线中枢均线周期（默认 20）
     * k: 布林线上下轨标准差倍数（默认 2）
     * threshold: 窄通道阈值，定义为 (upper - lower) / middle <= threshold，例如 0.04 表示宽度 <= 4%
     * allowedExceedDays: 允许在检测窗口中有不超过 N 天宽度“超过阈值”的违规天数（容忍离散波动），默认 0 不容忍。
     */
    public List<BollingerNarrowResult> findNarrowChannelStocks(int lookbackDays, int period, double k, double threshold, int allowedExceedDays) {
        List<BollingerNarrowResult> results = new ArrayList<>();

        // 读取所有股票元信息
        Iterable<StocksInfoMaster> allInfos = infoRepo.findAll();

        for (StocksInfoMaster info : allInfos) {
            String stockCode = info.getStockCode();
            if (stockCode == null || stockCode.isEmpty()) {
                continue;
            }
            // 最近 lookbackDays 的数据，按 trade_date DESC 获取后再按日期 ASC 排序以构建序列
            List<StocksDailyData> latestDesc = dailyRepo.listByStockCode(stockCode, lookbackDays, 0);
            if (latestDesc == null || latestDesc.isEmpty()) {
                continue;
            }
            List<StocksDailyData> latestAsc = latestDesc.stream()
                    .sorted(Comparator.comparing(StocksDailyData::getTradeDate))
                    .collect(Collectors.toList());

            // 构建 TA4J BarSeries（使用 BaseBarSeriesBuilder）
            BarSeries series = new BaseBarSeriesBuilder().withName(stockCode).build();
            for (StocksDailyData d : latestAsc) {
                Instant endTime = d.getTradeDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
                series.addBar(series.barBuilder()
                        .timePeriod(Duration.ofDays(1))
                        .endTime(endTime)
                        .openPrice(d.getOpenPrice() == null ? 0.0 : d.getOpenPrice().doubleValue())
                        .highPrice(d.getHighPrice() == null ? 0.0 : d.getHighPrice().doubleValue())
                        .lowPrice(d.getLowPrice() == null ? 0.0 : d.getLowPrice().doubleValue())
                        .closePrice(d.getClosePrice() == null ? 0.0 : d.getClosePrice().doubleValue())
                        .volume(d.getVolume() == null ? 0.0 : d.getVolume().doubleValue())
                        .build());
            }

            if (series.getBarCount() < period) {
                continue; // 数据不足以计算
            }

            // 指标（基于收盘价）
            ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
            SMAIndicator middle = new SMAIndicator(closePrice, period);
            StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, period);

            List<java.time.LocalDate> matchedDates = new ArrayList<>();
            int violationDays = 0;
            for (int i = 0; i < series.getBarCount(); i++) {
                if (i < period - 1) continue; // 指标尚未就绪
                double m = middle.getValue(i).doubleValue();
                double s = sd.getValue(i).doubleValue();
                double upper = m + k * s;
                double lower = m - k * s;
                double widthRatio = (upper - lower) / (m == 0.0 ? 1.0 : m);
                if (widthRatio <= threshold) {
                    matchedDates.add(series.getBar(i).getEndTime().atZone(ZoneId.systemDefault()).toLocalDate());
                } else {
                    violationDays++;
                }
            }

            // 仅当违规天数不超过允许值，且至少有一天满足窄通道时，纳入结果
            if (violationDays <= allowedExceedDays && !matchedDates.isEmpty()) {
                results.add(new BollingerNarrowResult(
                        stockCode,
                        matchedDates.size(),
                        matchedDates.get(matchedDates.size() - 1),
                        matchedDates
                ));
            }
        }

        // 可按匹配次数降序
        results.sort(Comparator.comparing(BollingerNarrowResult::getMatchCount).reversed());
        return results;
    }

    /**
     * 最近 N 天内收盘价位于中轨与上轨之间，允许不超过 allowedExceedDays 天不满足 —— 返回详细信息
     */
    public List<BollingerUpperChannelResult> findUpperChannelDetails(int lookbackDays, int period, double k, int allowedExceedDays) {
        List<BollingerUpperChannelResult> results = new ArrayList<>();
        Iterable<StocksInfoMaster> allInfos = infoRepo.findAll();
        for (StocksInfoMaster info : allInfos) {
            String stockCode = info.getStockCode();
            if (stockCode == null || stockCode.isEmpty()) continue;

            int need = lookbackDays + period; // 保证指标在整个窗口内就绪
            List<StocksDailyData> latestDesc = dailyRepo.listByStockCode(stockCode, need, 0);
            if (latestDesc == null || latestDesc.size() < period) continue;

            List<StocksDailyData> latestAsc = latestDesc.stream()
                    .sorted(Comparator.comparing(StocksDailyData::getTradeDate))
                    .collect(Collectors.toList());

            BarSeries series = new BaseBarSeriesBuilder().withName(stockCode).build();
            for (StocksDailyData d : latestAsc) {
                Instant endTime = d.getTradeDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
                series.addBar(series.barBuilder()
                        .timePeriod(Duration.ofDays(1))
                        .endTime(endTime)
                        .openPrice(d.getOpenPrice() == null ? 0.0 : d.getOpenPrice().doubleValue())
                        .highPrice(d.getHighPrice() == null ? 0.0 : d.getHighPrice().doubleValue())
                        .lowPrice(d.getLowPrice() == null ? 0.0 : d.getLowPrice().doubleValue())
                        .closePrice(d.getClosePrice() == null ? 0.0 : d.getClosePrice().doubleValue())
                        .volume(d.getVolume() == null ? 0.0 : d.getVolume().doubleValue())
                        .build());
            }
            if (series.getBarCount() < period) continue;

            ClosePriceIndicator close = new ClosePriceIndicator(series);
            SMAIndicator sma = new SMAIndicator(close, period);
            StandardDeviationIndicator sd = new StandardDeviationIndicator(close, period);
            BollingerBandsMiddleIndicator middle = new BollingerBandsMiddleIndicator(sma);

            int startIndex = Math.max(period - 1, series.getBarCount() - lookbackDays);
            int within = 0;
            int violations = 0;
            List<java.time.LocalDate> matchedDates = new ArrayList<>();
            java.time.LocalDate lastMatchedDate = null;

            for (int i = startIndex; i < series.getBarCount(); i++) {
                double c = close.getValue(i).doubleValue();
                double m = middle.getValue(i).doubleValue();
                double s = sd.getValue(i).doubleValue();
                double u = m + k * s;
                if (c >= m && c <= u) {
                    within++;
                    java.time.LocalDate d = series.getBar(i).getEndTime().atZone(ZoneId.systemDefault()).toLocalDate();
                    matchedDates.add(d);
                    lastMatchedDate = d;
                } else {
                    violations++;
                }
            }
            if (violations <= allowedExceedDays) {
                results.add(new BollingerUpperChannelResult(stockCode, within, violations, lastMatchedDate, matchedDates));
            }
        }
        results.sort(Comparator.comparingInt(BollingerUpperChannelResult::getWithinRangeCount)
                .thenComparingInt(BollingerUpperChannelResult::getViolationDays));
        java.util.Collections.reverse(results);
        return results;
     }

    /**
     * 最近 N 天内收盘价位于中轨与上轨之间，允许不超过 allowedExceedDays 天不满足 —— 返回股票代码
     */
    public List<String> findUpperChannelStockCodes(int lookbackDays, int period, double k, int allowedExceedDays) {
        return findUpperChannelDetails(lookbackDays, period, k, allowedExceedDays)
                .stream().map(BollingerUpperChannelResult::getStockCode).collect(Collectors.toList());
     }
}