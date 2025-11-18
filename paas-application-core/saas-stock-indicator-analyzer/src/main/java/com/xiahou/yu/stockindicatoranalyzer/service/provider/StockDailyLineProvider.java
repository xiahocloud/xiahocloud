package com.xiahou.yu.stockindicatoranalyzer.service.provider;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksDailyDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockDailyLineProvider implements DailyLineProvider {

    private final StocksDailyDataRepository repository;

    public StockDailyLineProvider(StocksDailyDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DailyLineDTO> listByCodeBetween(String code, LocalDate start, LocalDate end) {
        List<StocksDailyData> list = repository.findByStockCodeAndTradeDateBetweenOrderByTradeDateAsc(code, start, end);
        return list.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<DailyLineDTO> listLatestAsc(String code, int limit) {
        List<StocksDailyData> allAsc = repository.findByStockCodeOrderByTradeDateAsc(code);
        List<StocksDailyData> tail = allAsc.stream()
                .sorted(Comparator.comparing(StocksDailyData::getTradeDate).reversed())
                .limit(limit)
                .sorted(Comparator.comparing(StocksDailyData::getTradeDate))
                .collect(Collectors.toList());
        return tail.stream().map(this::map).collect(Collectors.toList());
    }

    private DailyLineDTO map(StocksDailyData s) {
        return DailyLineDTO.builder()
                .code(s.getStockCode())
                .tradeDate(s.getTradeDate())
                .open(s.getOpenPrice())
                .high(s.getHighPrice())
                .low(s.getLowPrice())
                .close(s.getClosePrice())
                .volume(s.getVolume())
                .turnover(s.getTurnover())
                .changeAmount(s.getChangeAmount())
                .changePercentage(s.getChangePercentage())
                .turnoverRate(s.getTurnoverRate())
                .build();
    }
}