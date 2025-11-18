package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksDailyDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StocksDailyDataService {
    private final StocksDailyDataRepository repository;

    public StocksDailyData save(StocksDailyData data) {
        return repository.save(data);
    }

    public Optional<StocksDailyData> get(String stockCode, LocalDate date) {
        return repository.findByStockCodeAndTradeDate(stockCode, date);
    }

    public List<StocksDailyData> list(String stockCode) {
        return repository.findByStockCodeOrderByTradeDateAsc(stockCode);
    }
}

