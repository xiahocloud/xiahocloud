package com.xiahou.yu.stockindicatoranalyzer.calculator;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基于 DTO 的 MACD 计算器：填充 macdDif/macdDea/macdHist。
 */
public class MacdDtoCalculator {

    public void apply(List<DailyLineDTO> list, int fastPeriod, int slowPeriod, int signalPeriod) {
        if (list == null || list.isEmpty()) return;
        BarSeries series = Ta4jSeriesMapper.buildSeries("DTO", list);
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        MACDIndicator macdDif = new MACDIndicator(close, fastPeriod, slowPeriod);
        EMAIndicator macdDea = new EMAIndicator(macdDif, signalPeriod);
        for (int i = 0; i < series.getBarCount() && i < list.size(); i++) {
            Num dif = macdDif.getValue(i);
            Num dea = macdDea.getValue(i);
            Num hist = dif.minus(dea);
            DailyLineDTO d = list.get(i);
            d.setMacdDif(BigDecimal.valueOf(dif.doubleValue()));
            d.setMacdDea(BigDecimal.valueOf(dea.doubleValue()));
            d.setMacdHist(BigDecimal.valueOf(hist.doubleValue()));
        }
    }
}