package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndustryIndexesDailyData;
import com.xiahou.yu.stockindicatoranalyzer.repository.IndustryIndexesDailyDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndustryIndexesDailyDataService {

    private final IndustryIndexesDailyDataRepository repository;

    public IndustryIndexesDailyData saveOrUpdate(IndustryIndexesDailyData data) {
        Optional<IndustryIndexesDailyData> existing = repository.findByIndexCodeAndTradeDate(data.getIndexCode(), data.getTradeDate());
        if (existing.isPresent()) {
            IndustryIndexesDailyData old = existing.get();
            data.setId(old.getId());
            data.setCreatedAt(old.getCreatedAt());
            data.setUpdatedAt(LocalDateTime.now());
        }
        return repository.save(data);
    }

    public Optional<IndustryIndexesDailyData> get(String indexCode, LocalDate date) {
        return repository.findByIndexCodeAndTradeDate(indexCode, date);
    }

    public Optional<IndustryIndexesDailyData> latest(String indexCode) {
        return repository.findLatestByIndexCode(indexCode);
    }

    public List<IndustryIndexesDailyData> listByIndexCode(String indexCode) {
        return repository.findByIndexCodeOrderByTradeDateAsc(indexCode);
    }

    public List<IndustryIndexesDailyData> rangeByIndexCode(String indexCode, LocalDate start, LocalDate end) {
        return repository.findByIndexCodeAndTradeDateBetweenOrderByTradeDateAsc(indexCode, start, end);
    }

    public List<IndustryIndexesDailyData> listByIndustryName(String industryName) {
        return repository.findByIndustryNameOrderByTradeDateAsc(industryName);
    }

    // 分页
    public List<IndustryIndexesDailyData> listAll(Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listAll(s, offset);
    }

    public long countAll() {
        return repository.countAll();
    }

    public List<IndustryIndexesDailyData> listByCode(String indexCode, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listByIndexCode(indexCode, s, offset);
    }

    public long countByCode(String indexCode) {
        return repository.countByIndexCode(indexCode);
    }

    public List<IndustryIndexesDailyData> listByCodeAndDateRange(String indexCode, LocalDate start, LocalDate end, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listByIndexCodeAndDateRange(indexCode, start, end, s, offset);
    }

    public long countByCodeAndDateRange(String indexCode, LocalDate start, LocalDate end) {
        return repository.countByIndexCodeAndDateRange(indexCode, start, end);
    }
}