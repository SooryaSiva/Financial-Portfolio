package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    @Test
    @DisplayName("costBasis: quantity and buyPrice present -> returns product with expected scale")
    void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
        Asset asset = Asset.builder()
                .quantity(new BigDecimal("10"))
                .buyPrice(new BigDecimal("150.00"))
                .build();

        BigDecimal expected = new BigDecimal("1500.00"); // scale 2 (0 + 2)
        assertEquals(expected, asset.getCostBasis());
    }

    @Test
    @DisplayName("costBasis: decimal quantity and price -> preserves expected precision")
    void costBasis_withDecimalValues_returnsPreciseProduct() {
        Asset asset = Asset.builder()
                .quantity(new BigDecimal("1.2345"))   // scale 4
                .buyPrice(new BigDecimal("12.34"))    // scale 2
                .build();

        BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
        assertEquals(expected, asset.getCostBasis());
    }

    @Test
    @DisplayName("costBasis: null quantity -> returns zero")
    void costBasis_whenQuantityNull_returnsZero() {
        Asset asset = Asset.builder()
                .quantity(null)
                .buyPrice(new BigDecimal("100.00"))
                .build();

        assertEquals(BigDecimal.ZERO, asset.getCostBasis());
    }

    @Test
    @DisplayName("costBasis: null buyPrice -> returns zero")
    void costBasis_whenBuyPriceNull_returnsZero() {
        Asset asset = Asset.builder()
                .quantity(new BigDecimal("2"))
                .buyPrice(null)
                .build();

        assertEquals(BigDecimal.ZERO, asset.getCostBasis());
    }

    @Test
    @DisplayName("costBasis: both quantity and buyPrice null -> returns zero")
    void costBasis_whenBothNull_returnsZero() {
        Asset asset = Asset.builder()
                .quantity(null)
                .buyPrice(null)
                .build();

        assertEquals(BigDecimal.ZERO, asset.getCostBasis());
    }
}
