package com.xiahou.yu.stockindicatoranalyzer.calculator;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import java.math.BigDecimal;
import java.util.List;

/**
 * 基于 DTO 的布林带计算器：填充 bbUpper/bbMiddle/bbLower。
 */
public class BollingerDtoCalculator {

    public void apply(List<DailyLineDTO> list, int period, double k) {
        if (list == null || list.isEmpty()) return;
        BarSeries series = Ta4jSeriesMapper.buildSeries("DTO", list);
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        SMAIndicator middle = new SMAIndicator(close, period);
        StandardDeviationIndicator sd = new StandardDeviationIndicator(close, period);
        for (int i = 0; i < series.getBarCount() && i < list.size(); i++) {
            double m = middle.getValue(i).doubleValue();
            double s = sd.getValue(i).doubleValue();
            double u = m + k * s;
            double l = m - k * s;
            DailyLineDTO d = list.get(i);
            d.setBbMiddle(BigDecimal.valueOf(m));
            d.setBbUpper(BigDecimal.valueOf(u));
            d.setBbLower(BigDecimal.valueOf(l));
        }
    }
}