package com.xiahou.yu.stockindicatoranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易量对比结果：最近N天 vs 之前M天
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeCompareResult {
    private String stockCode;
    private long recentSum;
    private long previousSum;
    private double ratio; // recentSum / previousSum
}