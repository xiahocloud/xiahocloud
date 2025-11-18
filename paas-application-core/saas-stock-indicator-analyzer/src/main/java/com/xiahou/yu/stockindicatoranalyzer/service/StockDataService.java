package com.xiahou.yu.stockindicatoranalyzer.service;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksDailyDataRepository;
import com.xiahou.yu.stockindicatoranalyzer.repository.StocksInfoMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockDataService {

    private final StocksInfoMasterRepository infoRepo;
    private final StocksDailyDataRepository dailyRepo;

    // 基础元数据保存/查询
    public StocksInfoMaster saveInfo(StocksInfoMaster info) {
        return infoRepo.save(info);
    }

    public Optional<StocksInfoMaster> findInfoByCode(String stockCode) {
        return infoRepo.findByStockCode(stockCode);
    }

    // 分页 list：pageNum 从 1 开始
    public List<StocksInfoMaster> listInfos(Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 20 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return infoRepo.listAll(s, offset);
    }

    public long countInfos() {
        return infoRepo.countAll();
    }

    public List<StocksInfoMaster> listInfos(String exchange, String assetType, String status, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 20 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return infoRepo.listWithFilters(exchange, assetType, status, s, offset);
    }

    public long countInfos(String exchange, String assetType, String status) {
        return infoRepo.countWithFilters(exchange, assetType, status);
    }

    // 每日行情数据保存：根据唯一索引 (stock_code, trade_date) 实现幂等保存（存在则更新，不存在则插入）
    public StocksDailyData saveOrUpdateDaily(StocksDailyData data) {
        Optional<StocksDailyData> existing = dailyRepo.findByStockCodeAndTradeDate(data.getStockCode(), data.getTradeDate());
        if (existing.isPresent()) {
            StocksDailyData old = existing.get();
            data.setId(old.getId());
            // 保留 createdAt，刷新 updatedAt
            data.setCreatedAt(old.getCreatedAt());
            data.setUpdatedAt(LocalDateTime.now());
        }
        return dailyRepo.save(data);
    }

    public Optional<StocksDailyData> getLatestDaily(String stockCode) {
        return dailyRepo.findLatestByStockCode(stockCode);
    }

    public List<StocksDailyData> getDailyRange(String stockCode, LocalDate start, LocalDate end) {
        return dailyRepo.findByStockCodeAndTradeDateBetweenOrderByTradeDateAsc(stockCode, start, end);
    }

    // 分页 list 每日行情：pageNum 从 1 开始
    public List<StocksDailyData> listDaily(Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return dailyRepo.listAll(s, offset);
    }

    public long countDaily() {
        return dailyRepo.countAll();
    }

    public List<StocksDailyData> listDaily(String stockCode, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return dailyRepo.listByStockCode(stockCode, s, offset);
    }

    public long countDaily(String stockCode) {
        return dailyRepo.countByStockCode(stockCode);
    }

    public List<StocksDailyData> listDaily(String stockCode, LocalDate start, LocalDate end, Integer pageNum, Integer pageSize) {
        int s = pageSize == null || pageSize <= 0 ? 50 : pageSize;
        int p = pageNum == null || pageNum <= 0 ? 1 : pageNum;
        int offset = (p - 1) * s;
        return dailyRepo.listByStockCodeAndDateRange(stockCode, start, end, s, offset);
    }

    public long countDaily(String stockCode, LocalDate start, LocalDate end) {
        return dailyRepo.countByStockCodeAndDateRange(stockCode, start, end);
    }
}