package com.xiahou.yu.stockindicatoranalyzer.service.dtoanalysis;

import com.xiahou.yu.stockindicatoranalyzer.calculator.BollingerDtoCalculator;
import com.xiahou.yu.stockindicatoranalyzer.calculator.EmaDtoCalculator;
import com.xiahou.yu.stockindicatoranalyzer.calculator.MacdDtoCalculator;
import com.xiahou.yu.stockindicatoranalyzer.calculator.SmaDtoCalculator;
import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;

import java.util.List;

/**
 * 基于统一 DTO 的指标增强基类：对列表进行 SMA/EMA/MACD/BOLL 统一计算。
 */
public abstract class BaseDtoIndicatorAnalysisService {

    private final SmaDtoCalculator smaCalculator = new SmaDtoCalculator();
    private final EmaDtoCalculator emaCalculator = new EmaDtoCalculator();
    private final MacdDtoCalculator macdCalculator = new MacdDtoCalculator();
    private final BollingerDtoCalculator bollCalculator = new BollingerDtoCalculator();

    /**
     * 对 DTO 列表进行指标增强（原地填充 DTO 指标字段）
     */
    protected void augmentIndicators(List<DailyLineDTO> list) {
        // 保持兼容：默认 period=20, k=2.0
        augmentIndicators(list, 20, 2.0);
    }

    /**
     * 指标增强（可配置 BOLL 参数）
     */
    protected void augmentIndicators(List<DailyLineDTO> list, int period, double k) {
        if (list == null || list.isEmpty()) return;
        smaCalculator.apply(list);
        emaCalculator.apply(list);
        macdCalculator.apply(list, 12, 26, 9);
        // 使用传入的布林参数
        bollCalculator.apply(list, period, k);
    }
}