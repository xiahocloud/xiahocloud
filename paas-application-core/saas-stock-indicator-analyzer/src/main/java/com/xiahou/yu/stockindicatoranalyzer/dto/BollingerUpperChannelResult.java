package com.xiahou.yu.stockindicatoranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 最近若干天内，收盘价位于布林线中轨与上轨之间的检测结果。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BollingerUpperChannelResult {
    private String stockCode;
    private int withinRangeCount;      // 满足 (close ∈ [middle, upper]) 的天数
    private int violationDays;         // 不满足条件的天数（允许不超过 allowedExceedDays）
    private LocalDate lastMatchedDate; // 最近一次满足条件的日期
    private List<LocalDate> matchedDates; // 所有满足条件的日期集合（最近窗口内）
}