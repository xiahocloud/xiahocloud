package com.xiahou.yu.stockindicatoranalyzer.repository;

import com.xiahou.yu.stockindicatoranalyzer.entity.StocksInfoMaster;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StocksInfoMasterRepository extends CrudRepository<StocksInfoMaster, Long> {
    Optional<StocksInfoMaster> findByStockCode(String stockCode);

    // 通用列表与计数
    @Query("SELECT * FROM stocks_info_master ORDER BY id ASC LIMIT :limit OFFSET :offset")
    List<StocksInfoMaster> listAll(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM stocks_info_master")
    long countAll();

    @Query("SELECT * FROM stocks_info_master WHERE (:exchange IS NULL OR exchange = :exchange) AND (:assetType IS NULL OR asset_type = :assetType) AND (:status IS NULL OR status = :status) ORDER BY id ASC LIMIT :limit OFFSET :offset")
    List<StocksInfoMaster> listWithFilters(@Param("exchange") String exchange,
                                           @Param("assetType") String assetType,
                                           @Param("status") String status,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM stocks_info_master WHERE (:exchange IS NULL OR exchange = :exchange) AND (:assetType IS NULL OR asset_type = :assetType) AND (:status IS NULL OR status = :status)")
    long countWithFilters(@Param("exchange") String exchange,
                          @Param("assetType") String assetType,
                          @Param("status") String status);
}

