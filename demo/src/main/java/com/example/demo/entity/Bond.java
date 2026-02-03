package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a bond asset.
 * Joined to 'assets' table via FK on id.
 */
@Entity
@Table(name = "bonds")
@DiscriminatorValue("BOND")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Bond extends BaseAsset {

    @Column(name = "coupon_rate", precision = 5, scale = 2)
    private BigDecimal couponRate; // Annual coupon rate %

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "issuer", length = 100)
    private String issuer; // Government, Corporate name, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "bond_type", length = 30)
    private BondType bondType;

    @Column(name = "credit_rating", length = 10)
    private String creditRating; // AAA, AA, A, BBB, etc.

    @Override
    public AssetType getType() {
        return AssetType.BOND;
    }

    public enum BondType {
        GOVERNMENT,
        CORPORATE,
        MUNICIPAL,
        TREASURY,
        AGENCY
    }
}
