package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Entity representing a real estate asset.
 * Joined to 'assets' table via FK on id.
 */
@Entity
@Table(name = "real_estates")
@DiscriminatorValue("REAL_ESTATE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RealEstate extends BaseAsset {

    @Column(name = "property_address", length = 200)
    private String propertyAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 30)
    private PropertyType propertyType;

    @Column(name = "rental_income", precision = 19, scale = 2)
    private BigDecimal rentalIncome; // Monthly rental income

    @Column(name = "square_footage")
    private Integer squareFootage;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Override
    public AssetType getType() {
        return AssetType.REAL_ESTATE;
    }

    public enum PropertyType {
        RESIDENTIAL,
        COMMERCIAL,
        INDUSTRIAL,
        LAND,
        REIT
    }
}
