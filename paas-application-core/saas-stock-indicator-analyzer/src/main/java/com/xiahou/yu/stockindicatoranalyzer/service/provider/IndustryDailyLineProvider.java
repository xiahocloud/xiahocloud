package com.xiahou.yu.stockindicatoranalyzer.service.provider;

import com.xiahou.yu.stockindicatoranalyzer.dto.DailyLineDTO;
import com.xiahou.yu.stockindicatoranalyzer.entity.IndustryIndexesDailyData;
import com.xiahou.yu.stockindicatoranalyzer.repository.IndustryIndexesDailyDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndustryDailyLineProvider implements DailyLineProvider {

    private final IndustryIndexesDailyDataRepository repository;

    public IndustryDailyLineProvider(IndustryIndexesDailyDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DailyLineDTO> listByCodeBetween(String code, LocalDate start, LocalDate end) {
        List<IndustryIndexesDailyData> list = repository.findByIndexCodeAndTradeDateBetweenOrderByTradeDateAsc(code, start, end);
        return list.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public List<DailyLineDTO> listLatestAsc(String code, int limit) {
        List<IndustryIndexesDailyData> allAsc = repository.findByIndexCodeOrderByTradeDateAsc(code);
        List<IndustryIndexesDailyData> tail = allAsc.stream()
                .sorted(Comparator.comparing(IndustryIndexesDailyData::getTradeDate).reversed())
                .limit(limit)
                .sorted(Comparator.comparing(IndustryIndexesDailyData::getTradeDate))
                .collect(Collectors.toList());
        return tail.stream().map(this::map).collect(Collectors.toList());
    }

    private DailyLineDTO map(IndustryIndexesDailyData s) {
        return DailyLineDTO.builder()
                .code(s.getIndexCode())
                .name(s.getIndustryName())
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