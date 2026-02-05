package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EtfTest {

    @Test
    @DisplayName("getType returns AssetType.ETF")
    void getType_returnsEtf() {
        Etf e = Etf.builder()
                .symbol("ETF1")
                .name("Test ETF")
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals(AssetType.ETF, e.getType());
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            Etf e = Etf.builder()
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .build();

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(0, expected.compareTo(e.getCostBasis()));
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            Etf e = Etf.builder()
                    .quantity(new BigDecimal("1.2345"))   // scale 4
                    .buyPrice(new BigDecimal("12.34"))    // scale 2
                    .build();

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(0, expected.compareTo(e.getCostBasis()));
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            Etf e = Etf.builder()
                    .quantity(null)
                    .buyPrice(new BigDecimal("100.00"))
                    .build();

            assertEquals(BigDecimal.ZERO, e.getCostBasis());
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            Etf e = Etf.builder()
                    .quantity(new BigDecimal("2"))
                    .buyPrice(null)
                    .build();

            assertEquals(BigDecimal.ZERO, e.getCostBasis());
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            Etf e = Etf.builder()
                    .quantity(new BigDecimal("-3"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(0, expected.compareTo(e.getCostBasis()));
        }
    }

    @Test
    @DisplayName("builder sets Etf-specific fields and getters return them")
    void builder_setsEtfFields() {
        Etf e = Etf.builder()
                .exchange("NYSE")
                .expenseRatio(new BigDecimal("0.0003"))
                .category("Index")
                .holdingsCount(500)
                .dividendYield(new BigDecimal("1.50"))
                .quantity(new BigDecimal("100"))
                .buyPrice(new BigDecimal("2.00"))
                .build();

        assertEquals("NYSE", e.getExchange());
        assertEquals("Index", e.getCategory());
        assertEquals(Integer.valueOf(500), e.getHoldingsCount());
        assertEquals(0, new BigDecimal("0.0003").compareTo(e.getExpenseRatio()));
        assertEquals(0, new BigDecimal("1.50").compareTo(e.getDividendYield()));
    }
}
