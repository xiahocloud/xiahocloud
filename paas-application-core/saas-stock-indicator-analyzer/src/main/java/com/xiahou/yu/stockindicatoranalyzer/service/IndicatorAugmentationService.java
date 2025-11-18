package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksDailyDataRepository;
import com.xiahou.yu.stockindicatoranalyzer.strategy.BollingerIndicatorStrategy;
import com.xiahou.yu.stockindicatoranalyzer.strategy.EmaIndicatorStrategy;
import com.xiahou.yu.stockindicatoranalyzer.strategy.IndicatorAugmentationStrategy;
import com.xiahou.yu.stockindicatoranalyzer.strategy.SmaIndicatorStrategy;
import com.xiahou.yu.stockindicatoranalyzer.strategy.MacdIndicatorStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * 指标计算编排服务：
 * 1) 读取指定股票代码与日期范围的日线数据；
 * 2) 构建 ta4j BarSeries；
 * 3) 应用策略模式的四个实现（布林带、SMA、EMA、MACD），将指标填充到非数据库字段；
 * 4) 返回已填充的列表。
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IndicatorAugmentationService {

    private final StocksDailyDataRepository dailyRepo;

    @Value("${indicator.macd.shortPeriod}")
    private int macdShortPeriod;

    @Value("${indicator.macd.longPeriod}")
    private int macdLongPeriod;

    @Value("${indicator.macd.signalPeriod}")
    private int macdSignalPeriod;

    @Value("${indicator.macd.hist.multiplier}")
    private double macdHistMultiplier;

    /**
     * 拉取数据并填充指标（支持布林参数，MACD 使用默认配置）。
     */
    public List<StocksDailyData> augment(String stockCode, LocalDate start, LocalDate end, int bollingerPeriod, double bollingerK) {
        return augment(stockCode, start, end, bollingerPeriod, bollingerK, null, null, null, null);
    }

    /**
     * 拉取数据并填充指标（支持布林参数与 MACD 参数覆盖）。
     * @param stockCode 股票代码
     * @param start 起始交易日期（含）
     * @param end 结束交易日期（含）
     * @param bollingerPeriod 布林带周期
     * @param bollingerK 布林带倍数（标准差倍数）
     * @param macdShortOverride 可选覆盖：MACD 短期周期（默认读取配置）
     * @param macdLongOverride 可选覆盖：MACD 长期周期（默认读取配置）
     * @param macdSignalOverride 可选覆盖：MACD 信号线周期（默认读取配置）
     * @param macdHistMultiplierOverride 可选覆盖：MACD 柱乘数（默认读取配置）
     * @return 添加了非数据库指标字段的每日数据列表，日期升序
     */
    public List<StocksDailyData> augment(String stockCode, LocalDate start, LocalDate end, int bollingerPeriod, double bollingerK,
                                         Integer macdShortOverride, Integer macdLongOverride, Integer macdSignalOverride, Double macdHistMultiplierOverride) {
        List<StocksDailyData> list = dailyRepo.findByStockCodeAndTradeDateBetweenOrderByTradeDateAsc(stockCode, start, end);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        // 构建系列（日期升序）
        BarSeries series = new BaseBarSeriesBuilder().withName(stockCode).build();
        for (StocksDailyData d : list) {
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
        // 准备策略参数
        Map<String, Object> bollParams = new HashMap<>();
        bollParams.put("period", bollingerPeriod);
        bollParams.put("k", bollingerK);
        Map<String, Object> macdParams = new HashMap<>();
        macdParams.put("shortPeriod", macdShortOverride != null ? macdShortOverride : macdShortPeriod);
        macdParams.put("longPeriod", macdLongOverride != null ? macdLongOverride : macdLongPeriod);
        macdParams.put("signalPeriod", macdSignalOverride != null ? macdSignalOverride : macdSignalPeriod);
        macdParams.put("histMultiplier", macdHistMultiplierOverride != null ? macdHistMultiplierOverride : macdHistMultiplier);

        List<IndicatorAugmentationStrategy> strategies = Arrays.asList(
                new BollingerIndicatorStrategy(),
                new SmaIndicatorStrategy(),
                new EmaIndicatorStrategy(),
                new MacdIndicatorStrategy()
        );
        // 应用所有策略
        for (IndicatorAugmentationStrategy s : strategies) {
            Map<String, Object> params = Collections.emptyMap();
            if (s instanceof BollingerIndicatorStrategy) params = bollParams;
            else if (s instanceof MacdIndicatorStrategy) params = macdParams;
            s.augment(list, series, params);
        }
        return list;
    }
}