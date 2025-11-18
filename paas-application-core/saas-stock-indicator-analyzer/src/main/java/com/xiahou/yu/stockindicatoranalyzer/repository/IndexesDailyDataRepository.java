package com.xiahou.yu.stockindicatoranalyzer.repository;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesDailyData;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndexesDailyDataRepository extends CrudRepository<IndexesDailyData, Integer> {

    Optional<IndexesDailyData> findByIndexCodeAndTradeDate(String indexCode, LocalDate tradeDate);

    List<IndexesDailyData> findByIndexCodeOrderByTradeDateAsc(String indexCode);

    List<IndexesDailyData> findByIndexCodeAndTradeDateBetweenOrderByTradeDateAsc(String indexCode, LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM indexes_daily_data WHERE index_code = :indexCode ORDER BY trade_date DESC LIMIT 1")
    Optional<IndexesDailyData> findLatestByIndexCode(@Param("indexCode") String indexCode);

    // 通用分页列表与计数
    @Query("SELECT * FROM indexes_daily_data ORDER BY trade_date DESC LIMIT :limit OFFSET :offset")
    List<IndexesDailyData> listAll(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM indexes_daily_data")
    long countAll();

    @Query("SELECT * FROM indexes_daily_data WHERE index_code = :indexCode ORDER BY trade_date DESC LIMIT :limit OFFSET :offset")
    List<IndexesDailyData> listByIndexCode(@Param("indexCode") String indexCode, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM indexes_daily_data WHERE index_code = :indexCode")
    long countByIndexCode(@Param("indexCode") String indexCode);

    @Query("SELECT * FROM indexes_daily_data WHERE index_code = :indexCode AND trade_date BETWEEN :start AND :end ORDER BY trade_date ASC LIMIT :limit OFFSET :offset")
    List<IndexesDailyData> listByIndexCodeAndDateRange(@Param("indexCode") String indexCode,
                                                        @Param("start") LocalDate start,
                                                        @Param("end") LocalDate end,
                                                        @Param("limit") int limit,
                                                        @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM indexes_daily_data WHERE index_code = :indexCode AND trade_date BETWEEN :start AND :end")
    long countByIndexCodeAndDateRange(@Param("indexCode") String indexCode,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);
}