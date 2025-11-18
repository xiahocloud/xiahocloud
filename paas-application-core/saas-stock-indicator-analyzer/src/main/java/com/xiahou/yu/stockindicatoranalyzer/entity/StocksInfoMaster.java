package com.xiahou.yu.stockindicatoranalyzer.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Basic stock metadata entity mapped to table stocks_info_master.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stocks_info_master")
public class StocksInfoMaster {
    @Id
    private Long id;

    @Column("stock_code")
    private String stockCode;

    @Column("stock_name")
    private String stockName;

    @Column("short_name")
    private String shortName;

    private String exchange;
    private String market;

    @Column("asset_type")
    private String assetType;

    private String status; // default 'active'
    private String state; // nullable

    @Column("listing_date")
    private LocalDate listingDate; // nullable

    @Column("delisting_date")
    private LocalDate delistingDate; // nullable

    private String currency; // nullable

    @Column("industry_category")
    private String industryCategory; // nullable

    @Column("created_at")
    private LocalDateTime createdAt; // db default

    @Column("updated_at")
    private LocalDateTime updatedAt; // db default
}

