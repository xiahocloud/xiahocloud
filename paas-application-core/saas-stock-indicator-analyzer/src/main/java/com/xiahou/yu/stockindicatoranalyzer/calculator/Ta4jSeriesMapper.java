package com.xiahou.yu.stockindicatoranalyzer.calculator;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * 将 DailyLineDTO 列表映射为 TA4J 的 BarSeries。
 */
public final class Ta4jSeriesMapper {

    private Ta4jSeriesMapper() {}

    public static BarSeries buildSeries(String name, List<DailyLineDTO> list) {
        BarSeries series = new BaseBarSeriesBuilder().withName(name).build();
        for (DailyLineDTO d : list) {
            Instant endTime = d.getTradeDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
            series.addBar(series.barBuilder()
                    .timePeriod(Duration.ofDays(1))
                    .endTime(endTime)
                    .openPrice(toDouble(d.getOpen()))
                    .highPrice(toDouble(d.getHigh()))
                    .lowPrice(toDouble(d.getLow()))
                    .closePrice(toDouble(d.getClose()))
                    .volume(d.getVolume() == null ? 0.0 : d.getVolume().doubleValue())
                    .build());
        }
        return series;
    }

    private static double toDouble(java.math.BigDecimal v) {
        return v == null ? 0.0 : v.doubleValue();
    }
}