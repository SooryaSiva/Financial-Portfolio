package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing a cash/savings asset.
 */
@Entity
@Table(name = "cash_holdings")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cash extends BaseAsset {

    @Column(name = "currency", length = 3)
    private String currency; // USD, EUR, GBP, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", length = 30)
    private AccountType accountType;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate; // Annual interest rate %

    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Override
    public AssetType getType() {
        return AssetType.CASH;
    }

    public enum AccountType {
        SAVINGS,
        CHECKING,
        MONEY_MARKET,
        CD,
        HIGH_YIELD_SAVINGS
    }
}
