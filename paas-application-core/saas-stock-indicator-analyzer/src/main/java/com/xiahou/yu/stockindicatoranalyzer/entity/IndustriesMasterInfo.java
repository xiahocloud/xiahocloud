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
 * 映射表：industries_master_info （行业主表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("industries_master_info")
public class IndustriesMasterInfo {

    @Id
    private Integer id;

    @Column("industry_code")
    private String industryCode;

    @Column("industry_name")
    private String industryName;

    @Column("level")
    private String level;

    @Column("parent_code")
    private String parentCode;

    @Column("source")
    private String source;

    @Column("status")
    private String status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}