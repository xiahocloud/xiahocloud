package com.xiahou.yu.stockindicatoranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 统一日线 DTO：承载股票、指数、行业的日线数据与通用指标字段，便于统一计算。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyLineDTO {

    /** 标的代码：股票/指数/行业代码 */
    private String code;

    /** 标的名称（可选） */
    private String name;

    /** 交易日期 */
    private LocalDate tradeDate;

    /** OHLCV 与成交额 */
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
    private Long turnover;

    /** 涨跌与换手（可选） */
    private BigDecimal changeAmount;
    private BigDecimal changePercentage;
    private BigDecimal turnoverRate;

    // ================= 通用指标字段（计算后填充） =================
    // Bollinger Bands
    private BigDecimal bbUpper;
    private BigDecimal bbMiddle;
    private BigDecimal bbLower;

    // SMA
    private BigDecimal sma5;
    private BigDecimal sma10;
    private BigDecimal sma20;
    private BigDecimal sma60;

    // EMA
    private BigDecimal ema5;
    private BigDecimal ema10;
    private BigDecimal ema20;
    private BigDecimal ema60;

    // MACD
    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdHist;
}