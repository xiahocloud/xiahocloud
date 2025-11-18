package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesDailyData;
import com.xiahou.yu.stockindicatoranalyzer.repository.IndexesDailyDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndexesDailyDataService {

    private final IndexesDailyDataRepository repository;

    public IndexesDailyData saveOrUpdate(IndexesDailyData data) {
        Optional<IndexesDailyData> existing = repository.findByIndexCodeAndTradeDate(data.getIndexCode(), data.getTradeDate());
        if (existing.isPresent()) {
            IndexesDailyData old = existing.get();
            data.setId(old.getId());
            data.setCreatedAt(old.getCreatedAt());
            data.setUpdatedAt(LocalDateTime.now());
        }
        return repository.save(data);
    }

    public Optional<IndexesDailyData> get(String indexCode, LocalDate date) {
        return repository.findByIndexCodeAndTradeDate(indexCode, date);
    }

    public Optional<IndexesDailyData> latest(String indexCode) {
        return repository.findLatestByIndexCode(indexCode);
    }

    public List<IndexesDailyData> list(String indexCode) {
        return repository.findByIndexCodeOrderByTradeDateAsc(indexCode);
    }

    public List<IndexesDailyData> range(String indexCode, LocalDate start, LocalDate end) {
        return repository.findByIndexCodeAndTradeDateBetweenOrderByTradeDateAsc(indexCode, start, end);
    }

    // 分页
    public List<IndexesDailyData> listAll(Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listAll(s, offset);
    }

    public long countAll() {
        return repository.countAll();
    }

    public List<IndexesDailyData> listByCode(String indexCode, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listByIndexCode(indexCode, s, offset);
    }

    public long countByCode(String indexCode) {
        return repository.countByIndexCode(indexCode);
    }

    public List<IndexesDailyData> listByCodeAndDateRange(String indexCode, LocalDate start, LocalDate end, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return repository.listByIndexCodeAndDateRange(indexCode, start, end, s, offset);
    }

    public long countByCodeAndDateRange(String indexCode, LocalDate start, LocalDate end) {
        return repository.countByIndexCodeAndDateRange(indexCode, start, end);
    }
}