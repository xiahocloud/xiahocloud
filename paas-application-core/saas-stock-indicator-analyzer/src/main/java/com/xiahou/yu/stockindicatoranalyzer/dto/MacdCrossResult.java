package com.xiahou.yu.stockindicatoranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MacdCrossResult {
    private String stockCode;
    private List<MacdCrossEvent> events; // 最近窗口内的交叉事件列表
}