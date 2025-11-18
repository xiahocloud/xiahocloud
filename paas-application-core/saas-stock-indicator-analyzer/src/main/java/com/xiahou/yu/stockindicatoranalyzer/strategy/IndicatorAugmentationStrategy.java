package com.xiahou.yu.stockindicatoranalyzer.strategy;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import org.ta4j.core.BarSeries;

import java.util.List;
import java.util.Map;

/**
 * 指标计算的策略接口：对传入的日线数据列表按序列进行指标计算并填充到非数据库字段。
 */
public interface IndicatorAugmentationStrategy {
    /**
     * 执行指标计算与填充。
     * @param dailyList  按交易日升序的日线数据列表
     * @param series     与 dailyList 对齐的 ta4j 序列
     * @param params     策略参数（如 period、k 等），可为空
     */
    void augment(List<StocksDailyData> dailyList, BarSeries series, Map<String, Object> params);
}