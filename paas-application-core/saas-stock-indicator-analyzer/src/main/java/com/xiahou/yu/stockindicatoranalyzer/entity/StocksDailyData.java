package com.xiahou.yu.stockindicatoranalyzer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 映射表：stocks_daily_data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stocks_daily_data")
public class StocksDailyData {

    @Id
    private Integer id;

    @Column("stock_code")
    private String stockCode;

    @Column("trade_date")
    private LocalDate tradeDate;

    @Column("open_price")
    private BigDecimal openPrice;

    @Column("close_price")
    private BigDecimal closePrice;

    @Column("high_price")
    private BigDecimal highPrice;

    @Column("low_price")
    private BigDecimal lowPrice;

    @Column("volume")
    private Long volume;

    @Column("turnover")
    private Long turnover;

    @Column("change_amount")
    private BigDecimal changeAmount;

    @Column("change_percentage")
    private BigDecimal changePercentage;

    @Column("turnover_rate")
    private BigDecimal turnoverRate;

    @Column("total_market_value")
    private Long totalMarketValue;

    @Column("circulating_market_value")
    private Long circulatingMarketValue;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    // ================= 非数据库字段：技术指标 =================
    // 布林带：上、中、下轨
    @Transient
    private BigDecimal bbUpper;    // Upper Band

    @Transient
    private BigDecimal bbMiddle;   // Middle Band (SMA)

    @Transient
    private BigDecimal bbLower;    // Lower Band

    // 简单移动平均（SMA）：5, 10, 20, 60
    @Transient
    private BigDecimal sma5;

    @Transient
    private BigDecimal sma10;

    @Transient
    private BigDecimal sma20;

    @Transient
    private BigDecimal sma60;

    // 指数移动平均（EMA）：5, 10, 20, 60
    @Transient
    private BigDecimal ema5;

    @Transient
    private BigDecimal ema10;

    @Transient
    private BigDecimal ema20;

    @Transient
    private BigDecimal ema60;

    // MACD 指标：DIF、DEA、柱（Hist = DIF - DEA）
    @Transient
    private BigDecimal macdDif;    // DIF = EMA12 - EMA26

    @Transient
    private BigDecimal macdDea;    // DEA = EMA(DIF, 9)

    @Transient
    private BigDecimal macdHist;   // Histogram = DIF - DEA （A股常用也有 2*(DIF-DEA)，此处不乘2）
}
