package com.xiahou.yu.stockindicatoranalyzer.repository;

import com.xiahou.yu.stockindicatoranalyzer.entity.IndexesMasterInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexesMasterInfoRepository extends CrudRepository<IndexesMasterInfo, Integer> {

    Optional<IndexesMasterInfo> findByIndexCode(String indexCode);

    Optional<IndexesMasterInfo> findByIndexName(String indexName);

    @Query("SELECT * FROM indexes_master_info ORDER BY id ASC LIMIT :limit OFFSET :offset")
    List<IndexesMasterInfo> listAll(@Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM indexes_master_info")
    long countAll();
}