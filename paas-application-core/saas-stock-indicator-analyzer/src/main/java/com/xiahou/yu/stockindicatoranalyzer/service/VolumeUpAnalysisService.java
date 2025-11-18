package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.dto.VolumeUpResult;
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
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用TA4J方法查找“放量上涨”的股票：
 * 条件（某日i满足）：
 * 1) 成交量放大：volume(i) / SMA(volume, volumePeriod)(i) >= volumeRatioThreshold
 * 2) 价格上涨：close(i) > close(i-1)，或涨幅百分比 >= minPriceChangePercent
 * 支持在最近 lookbackDays 的区间内进行检测。
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VolumeUpAnalysisService {

    private final StocksInfoMasterRepository infoRepo;
    private final StocksDailyDataRepository dailyRepo;

    /**
     * 返回满足“放量上涨”条件的股票代码列表，按匹配次数降序、峰值放量比降序排序。
     */
    public List<String> findVolumeUpStockCodes(int lookbackDays, int volumePeriod, double volumeRatioThreshold, double minPriceChangePercent) {
        return findVolumeUpDetails(lookbackDays, volumePeriod, volumeRatioThreshold, minPriceChangePercent).stream()
                .map(VolumeUpResult::getStockCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回详细结果：包含匹配日期、峰值放量比及对应涨幅等信息。
     */
    public List<VolumeUpResult> findVolumeUpDetails(int lookbackDays, int volumePeriod, double volumeRatioThreshold, double minPriceChangePercent) {
        if (lookbackDays <= 0 || volumePeriod <= 1) {
            throw new IllegalArgumentException("lookbackDays必须>0且volumePeriod必须>1");
        }
        List<VolumeUpResult> results = new ArrayList<>();

        Iterable<StocksInfoMaster> allInfos = infoRepo.findAll();
        for (StocksInfoMaster info : allInfos) {
            String stockCode = info.getStockCode();
            if (stockCode == null || stockCode.isEmpty()) continue;

            // 为了构造指标，需要至少 volumePeriod 天的数据；额外留出 lookbackDays 检测窗口
            int need = lookbackDays + volumePeriod + 1; // 确保在最近 lookbackDays 内，SMA 就绪的天数都有检测机会
            List<StocksDailyData> latestDesc = dailyRepo.listByStockCode(stockCode, need, 0);
            if (latestDesc == null || latestDesc.size() < need) {
                continue; // 数据不足
            }

            // 构建 BarSeries（按日期升序）
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

            if (series.getBarCount() < need) {
                continue;
            }

            // 指标
            ClosePriceIndicator close = new ClosePriceIndicator(series);
            VolumeIndicator volume = new VolumeIndicator(series);
            SMAIndicator volSma = new SMAIndicator(volume, volumePeriod);

            List<java.time.LocalDate> matchedDates = new ArrayList<>();
            double peakRatio = 0.0;
            double peakPriceChange = 0.0; // 百分比

            int startIndex = Math.max(1, series.getBarCount() - lookbackDays); // 从最近lookbackDays开始，且至少从1开始以便比较前一天
            for (int i = startIndex; i < series.getBarCount(); i++) {
                if (i < volumePeriod) continue; // SMA尚未就绪
                double vol = volume.getValue(i).doubleValue();
                double volAvg = volSma.getValue(i).doubleValue();
                double ratio = (volAvg == 0.0 ? 0.0 : vol / volAvg);

                double prevClose = close.getValue(i - 1).doubleValue();
                double currClose = close.getValue(i).doubleValue();
                double priceChangePct = (prevClose == 0.0 ? 0.0 : (currClose / prevClose - 1.0) * 100.0);

                if (ratio >= volumeRatioThreshold && priceChangePct >= minPriceChangePercent) {
                    matchedDates.add(series.getBar(i).getEndTime().atZone(ZoneId.systemDefault()).toLocalDate());
                    if (ratio > peakRatio) {
                        peakRatio = ratio;
                        peakPriceChange = priceChangePct;
                    }
                }
            }

            if (!matchedDates.isEmpty()) {
                results.add(new VolumeUpResult(
                        stockCode,
                        matchedDates.size(),
                        matchedDates.get(matchedDates.size() - 1),
                        peakRatio,
                        peakPriceChange,
                        matchedDates
                ));
            }
        }

        // 排序：先按匹配次数降序，再按峰值放量比降序
        results.sort(Comparator.comparing(VolumeUpResult::getMatchCount).reversed()
                .thenComparing(Comparator.comparing(VolumeUpResult::getPeakVolumeRatio).reversed()));
        return results;
    }
}