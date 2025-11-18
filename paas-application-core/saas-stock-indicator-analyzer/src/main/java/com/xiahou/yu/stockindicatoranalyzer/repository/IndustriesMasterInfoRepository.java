package com.xiahou.yu.stockindicatoranalyzer.repository;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndustriesMasterInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndustriesMasterInfoRepository extends CrudRepository<IndustriesMasterInfo, Integer> {

    Optional<IndustriesMasterInfo> findByIndustryCode(String industryCode);

    Optional<IndustriesMasterInfo> findByIndustryName(String industryName);

    // 通用分页列表与计数
    @Query("SELECT * FROM industries_master_info ORDER BY id ASC LIMIT :limit OFFSET :offset")
    List<IndustriesMasterInfo> listAll(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM industries_master_info")
    long countAll();

    @Query("SELECT * FROM industries_master_info WHERE (:status IS NULL OR status = :status) AND (:level IS NULL OR level = :level) ORDER BY id ASC LIMIT :limit OFFSET :offset")
    List<IndustriesMasterInfo> listWithFilters(@Param("status") String status,
                                               @Param("level") String level,
                                               @Param("limit") int limit,
                                               @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM industries_master_info WHERE (:status IS NULL OR status = :status) AND (:level IS NULL OR level = :level)")
    long countWithFilters(@Param("status") String status,
                          @Param("level") String level);
}