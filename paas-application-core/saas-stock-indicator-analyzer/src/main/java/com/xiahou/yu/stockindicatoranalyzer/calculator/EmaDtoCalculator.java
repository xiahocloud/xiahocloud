package com.xiahou.yu.stockindicatoranalyzer.calculator;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基于 DTO 的 EMA(EXPMA) 计算器：填充 ema5/10/20/60。
 */
public class EmaDtoCalculator {

    public void apply(List<DailyLineDTO> list) {
        if (list == null || list.isEmpty()) return;
        BarSeries series = Ta4jSeriesMapper.buildSeries("DTO", list);
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        EMAIndicator ema5 = new EMAIndicator(close, 5);
        EMAIndicator ema10 = new EMAIndicator(close, 10);
        EMAIndicator ema20 = new EMAIndicator(close, 20);
        EMAIndicator ema60 = new EMAIndicator(close, 60);
        for (int i = 0; i < series.getBarCount() && i < list.size(); i++) {
            DailyLineDTO d = list.get(i);
            d.setEma5(toBd(ema5.getValue(i)));
            d.setEma10(toBd(ema10.getValue(i)));
            d.setEma20(toBd(ema20.getValue(i)));
            d.setEma60(toBd(ema60.getValue(i)));
        }
    }

    private BigDecimal toBd(org.ta4j.core.num.Num n) {
        return BigDecimal.valueOf(n.doubleValue());
    }
}