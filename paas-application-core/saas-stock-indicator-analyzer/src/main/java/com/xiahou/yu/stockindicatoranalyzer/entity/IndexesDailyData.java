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
 * 映射表：indexes_daily_data （大盘/指数日线数据）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("indexes_daily_data")
public class IndexesDailyData {

    @Id
    private Integer id;

    @Column("index_code")
    private String indexCode;

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

    // ============== 非数据库字段：可复用的技术指标字段（便于和股票一致的分析） ==============
    @Transient
    private BigDecimal bbUpper;
    @Transient
    private BigDecimal bbMiddle;
    @Transient
    private BigDecimal bbLower;

    @Transient
    private BigDecimal sma5;
    @Transient
    private BigDecimal sma10;
    @Transient
    private BigDecimal sma20;
    @Transient
    private BigDecimal sma60;

    @Transient
    private BigDecimal ema5;
    @Transient
    private BigDecimal ema10;
    @Transient
    private BigDecimal ema20;
    @Transient
    private BigDecimal ema60;

    @Transient
    private BigDecimal macdDif;
    @Transient
    private BigDecimal macdDea;
    @Transient
    private BigDecimal macdHist;
}