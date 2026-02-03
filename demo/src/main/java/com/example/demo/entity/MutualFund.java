package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing a mutual fund asset.
 */
@Entity
@Table(name = "mutual_funds")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MutualFund extends BaseAsset {

    @Column(name = "fund_family", length = 50)
    private String fundFamily; // Vanguard, Fidelity, etc.

    @Column(name = "expense_ratio", precision = 5, scale = 4)
    private BigDecimal expenseRatio; // Annual expense ratio

    @Column(name = "category", length = 50)
    private String category; // Growth, Value, Blend, etc.

    @Column(name = "minimum_investment", precision = 19, scale = 2)
    private BigDecimal minimumInvestment;

    @Column(name = "dividend_yield", precision = 5, scale = 2)
    private BigDecimal dividendYield;

    @Override
    public AssetType getType() {
        return AssetType.MUTUAL_FUND;
    }
}
