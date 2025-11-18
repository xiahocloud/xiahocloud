package com.xiahou.yu.stockindicatoranalyzer.service.provider;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesDailyData;
import com.xiahou.yu.stockindicatoranalyzer.repository.IndexesDailyDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexDailyLineProvider implements DailyLineProvider {

    private final IndexesDailyDataRepository repository;

    public IndexDailyLineProvider(IndexesDailyDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DailyLineDTO> listByCodeBetween(String code, LocalDate start, LocalDate end) {
        List<IndexesDailyData> list = repository.findByIndexCodeAndTradeDateBetweenOrderByTradeDateAsc(code, start, end);
        return list.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<DailyLineDTO> listLatestAsc(String code, int limit) {
        List<IndexesDailyData> allAsc = repository.findByIndexCodeOrderByTradeDateAsc(code);
        List<IndexesDailyData> tail = allAsc.stream()
                .sorted(Comparator.comparing(IndexesDailyData::getTradeDate).reversed())
                .limit(limit)
                .sorted(Comparator.comparing(IndexesDailyData::getTradeDate))
                .collect(Collectors.toList());
        return tail.stream().map(this::map).collect(Collectors.toList());
    }

    private DailyLineDTO map(IndexesDailyData s) {
        return DailyLineDTO.builder()
                .code(s.getIndexCode())
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