package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BondTest {

    @Test
    @DisplayName("getType returns AssetType.BOND")
    void getType_returnsBond() {
        Bond b = Bond.builder()
                .symbol("BND")
                .name("Test Bond")
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals(AssetType.BOND, b.getType());
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            Bond b = Bond.builder()
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .build();

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(0, expected.compareTo(b.getCostBasis()));
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            Bond b = Bond.builder()
                    .quantity(new BigDecimal("1.2345"))   // scale 4
                    .buyPrice(new BigDecimal("12.34"))    // scale 2
                    .build();

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(0, expected.compareTo(b.getCostBasis()));
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            Bond b = Bond.builder()
                    .quantity(null)
                    .buyPrice(new BigDecimal("100.00"))
                    .build();

            assertEquals(BigDecimal.ZERO, b.getCostBasis());
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            Bond b = Bond.builder()
                    .quantity(new BigDecimal("2"))
                    .buyPrice(null)
                    .build();

            assertEquals(BigDecimal.ZERO, b.getCostBasis());
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            Bond b = Bond.builder()
                    .quantity(new BigDecimal("-3"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(0, expected.compareTo(b.getCostBasis()));
        }
    }

    @Test
    @DisplayName("BondType enum contains expected constants")
    void bondType_enumValuesPresent() {
        // simple presence checks
        assertNotNull(Bond.BondType.valueOf("GOVERNMENT"));
        assertNotNull(Bond.BondType.valueOf("CORPORATE"));

        // ensure a Bond instance can hold a BondType
        Bond b = Bond.builder()
                .bondType(Bond.BondType.TREASURY)
                .maturityDate(LocalDate.of(2030, 1, 1))
                .build();

        assertEquals(Bond.BondType.TREASURY, b.getBondType());
    }
}
