package com.xiahou.yu.stockindicatoranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 放量上涨检测结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolumeUpResult {
    private String stockCode;
    private int matchCount;
    private LocalDate lastMatchedDate;
    private double peakVolumeRatio; // 最近窗口内的最大成交量比 (当前成交量 / 均值)
    private double peakPriceChangePercent; // 对应日期的涨幅（百分比）
    private List<LocalDate> matchedDates;
}