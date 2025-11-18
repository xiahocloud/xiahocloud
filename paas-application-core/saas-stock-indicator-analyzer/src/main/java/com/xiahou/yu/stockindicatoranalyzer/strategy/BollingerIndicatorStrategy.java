package com.xiahou.yu.stockindicatoranalyzer.strategy;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 布林带指标计算策略：填充 bbUpper、bbMiddle、bbLower
 */
public class BollingerIndicatorStrategy implements IndicatorAugmentationStrategy {
    @Override
    public void augment(List<StocksDailyData> dailyList, BarSeries series, Map<String, Object> params) {
        if (dailyList == null || dailyList.isEmpty() || series == null || series.isEmpty()) {
            return;
        }
        int period = 20;
        double k = 2.0;
        if (params != null) {
            Object p = params.get("period");
            if (p instanceof Number) {
                period = ((Number) p).intValue();
            }
            Object kv = params.get("k");
            if (kv instanceof Number) {
                k = ((Number) kv).doubleValue();
            }
        }
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        SMAIndicator sma = new SMAIndicator(close, period);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(close, period);
        for (int i = 0; i < series.getBarCount() && i < dailyList.size(); i++) {
            double m = sma.getValue(i).doubleValue();
            double s = sd.getValue(i).doubleValue();
            double u = m + k * s;
            double l = m - k * s;
            StocksDailyData d = dailyList.get(i);
            d.setBbMiddle(BigDecimal.valueOf(m));
            d.setBbUpper(BigDecimal.valueOf(u));
            d.setBbLower(BigDecimal.valueOf(l));
        }
    }
}