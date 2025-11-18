package com.xiahou.yu.stockindicatoranalyzer.calculator;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基于 DTO 的 SMA/MA 计算器：填充 sma5/10/20/60。
 */
public class SmaDtoCalculator {

    public void apply(List<DailyLineDTO> list) {
        if (list == null || list.isEmpty()) return;
        BarSeries series = Ta4jSeriesMapper.buildSeries("DTO", list);
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        SMAIndicator sma5 = new SMAIndicator(close, 5);
        SMAIndicator sma10 = new SMAIndicator(close, 10);
        SMAIndicator sma20 = new SMAIndicator(close, 20);
        SMAIndicator sma60 = new SMAIndicator(close, 60);
        for (int i = 0; i < series.getBarCount() && i < list.size(); i++) {
            DailyLineDTO d = list.get(i);
            d.setSma5(toBd(sma5.getValue(i)));
            d.setSma10(toBd(sma10.getValue(i)));
            d.setSma20(toBd(sma20.getValue(i)));
            d.setSma60(toBd(sma60.getValue(i)));
        }
    }

    private BigDecimal toBd(org.ta4j.core.num.Num n) {
        return BigDecimal.valueOf(n.doubleValue());
    }
}