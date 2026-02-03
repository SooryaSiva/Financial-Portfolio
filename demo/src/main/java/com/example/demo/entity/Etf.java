package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing an ETF (Exchange-Traded Fund) asset.
 */
@Entity
@Table(name = "etfs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Etf extends BaseAsset {

    @Column(name = "exchange", length = 20)
    private String exchange; // NYSE, NASDAQ, etc.

    @Column(name = "expense_ratio", precision = 5, scale = 4)
    private BigDecimal expenseRatio; // Annual expense ratio (e.g., 0.0003 for 0.03%)

    @Column(name = "category", length = 50)
    private String category; // Index, Sector, Bond, Commodity, etc.

    @Column(name = "holdings_count")
    private Integer holdingsCount; // Number of holdings in the ETF

    @Column(name = "dividend_yield", precision = 5, scale = 2)
    private BigDecimal dividendYield; // Annual dividend yield %

    @Override
    public AssetType getType() {
        return AssetType.ETF;
    }
}
