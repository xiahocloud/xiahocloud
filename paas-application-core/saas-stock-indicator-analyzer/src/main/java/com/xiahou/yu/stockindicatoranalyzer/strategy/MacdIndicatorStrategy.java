package com.xiahou.yu.stockindicatoranalyzer.strategy;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * MACD 指标计算策略：填充 macdDif、macdDea、macdHist（柱 = multiplier × (DIF - DEA)）。
 * 参数默认：shortPeriod=12, longPeriod=26, signalPeriod=9。
 * multiplier（直方图倍数）默认 1.0，可通过 params.histMultiplier 配置；同时由 application.yml 的 indicator.macd.hist.multiplier 外部化。
 */
public class MacdIndicatorStrategy implements IndicatorAugmentationStrategy {
    @Override
    public void augment(List<StocksDailyData> dailyList, BarSeries series, Map<String, Object> params) {
        if (dailyList == null || dailyList.isEmpty() || series == null || series.isEmpty()) {
            return;
        }
        int shortPeriod = 12;
        int longPeriod = 26;
        int signalPeriod = 9;
        double histMultiplier = 1.0;
        if (params != null) {
            Object sp = params.get("shortPeriod");
            if (sp instanceof Number) shortPeriod = ((Number) sp).intValue();
            Object lp = params.get("longPeriod");
            if (lp instanceof Number) longPeriod = ((Number) lp).intValue();
            Object sgp = params.get("signalPeriod");
            if (sgp instanceof Number) signalPeriod = ((Number) sgp).intValue();
            Object hm = params.get("histMultiplier");
            if (hm instanceof Number) histMultiplier = ((Number) hm).doubleValue();
        }
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        MACDIndicator macd = new MACDIndicator(close, shortPeriod, longPeriod); // DIF
        EMAIndicator dea = new EMAIndicator(macd, signalPeriod); // DEA = EMA(DIF, 9)
        for (int i = 0; i < series.getBarCount() && i < dailyList.size(); i++) {
            double dif = macd.getValue(i).doubleValue();
            double signal = dea.getValue(i).doubleValue();
            double hist = (dif - signal) * histMultiplier; // 柱（可配置乘数）
            StocksDailyData d = dailyList.get(i);
            d.setMacdDif(BigDecimal.valueOf(dif));
            d.setMacdDea(BigDecimal.valueOf(signal));
            d.setMacdHist(BigDecimal.valueOf(hist));
        }
    }
}