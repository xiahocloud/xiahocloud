package com.xiahou.yu.stockindicatoranalyzer.strategy;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * SMA策略：填充 sma5、sma10、sma20、sma60
 */
public class SmaIndicatorStrategy implements IndicatorAugmentationStrategy {
    @Override
    public void augment(List<StocksDailyData> dailyList, BarSeries series, Map<String, Object> params) {
        if (dailyList == null || dailyList.isEmpty() || series == null || series.isEmpty()) {
            return;
        }
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        SMAIndicator sma5 = new SMAIndicator(close, 5);
        SMAIndicator sma10 = new SMAIndicator(close, 10);
        SMAIndicator sma20 = new SMAIndicator(close, 20);
        SMAIndicator sma60 = new SMAIndicator(close, 60);
        for (int i = 0; i < series.getBarCount() && i < dailyList.size(); i++) {
            StocksDailyData d = dailyList.get(i);
            d.setSma5(BigDecimal.valueOf(sma5.getValue(i).doubleValue()));
            d.setSma10(BigDecimal.valueOf(sma10.getValue(i).doubleValue()));
            d.setSma20(BigDecimal.valueOf(sma20.getValue(i).doubleValue()));
            d.setSma60(BigDecimal.valueOf(sma60.getValue(i).doubleValue()));
        }
    }
}