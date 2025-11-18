package com.xiahou.yu.stockindicatoranalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BollingerNarrowResult {
    private String stockCode;
    private int matchCount;
    private LocalDate lastMatchedDate;
    private List<LocalDate> matchedDates;
}