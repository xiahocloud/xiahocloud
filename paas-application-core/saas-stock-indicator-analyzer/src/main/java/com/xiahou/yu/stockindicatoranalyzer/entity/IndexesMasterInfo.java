package com.xiahou.yu.stockindicatoranalyzer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * 映射表：indexes_master_info （大盘/指数主表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("indexes_master_info")
public class IndexesMasterInfo {

    @Id
    private Integer id;

    @Column("index_code")
    private String indexCode; // 例如 sh.000001 / 000300.SH

    @Column("index_name")
    private String indexName; // 例如 上证指数 / 沪深300

    @Column("short_name")
    private String shortName; // 可选简称

    @Column("exchange")
    private String exchange; // 例如 SSE / SZSE

    @Column("source")
    private String source; // 数据来源（可选）

    @Column("status")
    private String status; // active / inactive

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}