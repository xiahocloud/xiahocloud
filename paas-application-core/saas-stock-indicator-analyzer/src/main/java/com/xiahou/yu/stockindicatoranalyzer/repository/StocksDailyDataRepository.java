package com.xiahou.yu.stockindicatoranalyzer.repository;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksDailyData;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StocksDailyDataRepository extends CrudRepository<StocksDailyData, Integer> {
    Optional<StocksDailyData> findByStockCodeAndTradeDate(String stockCode, LocalDate tradeDate);
    List<StocksDailyData> findByStockCodeOrderByTradeDateAsc(String stockCode);
    List<StocksDailyData> findByStockCodeAndTradeDateBetweenOrderByTradeDateAsc(String stockCode, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT * FROM stocks_daily_data WHERE stock_code = :stockCode ORDER BY trade_date DESC LIMIT 1")
    Optional<StocksDailyData> findLatestByStockCode(@Param("stockCode") String stockCode);
    
    // 通用列表与计数
    @Query("SELECT * FROM stocks_daily_data ORDER BY trade_date DESC LIMIT :limit OFFSET :offset")
    List<StocksDailyData> listAll(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM stocks_daily_data")
    long countAll();

    @Query("SELECT * FROM stocks_daily_data WHERE stock_code = :stockCode ORDER BY trade_date DESC LIMIT :limit OFFSET :offset")
    List<StocksDailyData> listByStockCode(@Param("stockCode") String stockCode, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM stocks_daily_data WHERE stock_code = :stockCode")
    long countByStockCode(@Param("stockCode") String stockCode);

    @Query("SELECT * FROM stocks_daily_data WHERE stock_code = :stockCode AND trade_date BETWEEN :startDate AND :endDate ORDER BY trade_date ASC LIMIT :limit OFFSET :offset")
    List<StocksDailyData> listByStockCodeAndDateRange(@Param("stockCode") String stockCode,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("limit") int limit,
                                                       @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM stocks_daily_data WHERE stock_code = :stockCode AND trade_date BETWEEN :startDate AND :endDate")
    long countByStockCodeAndDateRange(@Param("stockCode") String stockCode,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
}

