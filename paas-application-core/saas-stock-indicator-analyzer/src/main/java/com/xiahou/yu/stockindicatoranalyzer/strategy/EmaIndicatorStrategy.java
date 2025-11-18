package com.xiahou.yu.stockindicatoranalyzer.strategy;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * EMA策略：填充 ema5、ema10、ema20、ema60
 */
public class EmaIndicatorStrategy implements IndicatorAugmentationStrategy {
    @Override
    public void augment(List<StocksDailyData> dailyList, BarSeries series, Map<String, Object> params) {
        if (dailyList == null || dailyList.isEmpty() || series == null || series.isEmpty()) {
            return;
        }
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        EMAIndicator ema5 = new EMAIndicator(close, 5);
        EMAIndicator ema10 = new EMAIndicator(close, 10);
        EMAIndicator ema20 = new EMAIndicator(close, 20);
        EMAIndicator ema60 = new EMAIndicator(close, 60);
        for (int i = 0; i < series.getBarCount() && i < dailyList.size(); i++) {
            StocksDailyData d = dailyList.get(i);
            d.setEma5(BigDecimal.valueOf(ema5.getValue(i).doubleValue()));
            d.setEma10(BigDecimal.valueOf(ema10.getValue(i).doubleValue()));
            d.setEma20(BigDecimal.valueOf(ema20.getValue(i).doubleValue()));
            d.setEma60(BigDecimal.valueOf(ema60.getValue(i).doubleValue()));
        }
    }
}