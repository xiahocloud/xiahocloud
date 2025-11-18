package com.xiahou.yu.stockindicatoranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MacdCrossEvent {
    private LocalDate date;   // 事件发生日期
    private String type;      // "golden" 或 "death"
    private Double dif;       // 当日 DIF 值
    private Double dea;       // 当日 DEA 值
    private Double hist;      // 当日柱值（可能乘以配置的系数）
}