package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a stock/equity asset.
 */
@Entity
@Table(name = "stocks")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Stock extends BaseAsset {

    @Column(name = "exchange", length = 20)
    private String exchange; // NYSE, NASDAQ, etc.

    @Column(name = "sector", length = 50)
    private String sector; // Technology, Healthcare, etc.

    @Column(name = "dividend_yield", precision = 5, scale = 2)
    private java.math.BigDecimal dividendYield; // Annual dividend yield %

    @Column(name = "market_cap", length = 20)
    private String marketCap; // Large, Mid, Small

    @Override
    public AssetType getType() {
        return AssetType.STOCK;
    }
}
