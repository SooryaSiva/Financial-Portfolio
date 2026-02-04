package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    @DisplayName("getType returns AssetType.STOCK")
    void getType_returnsStock() {
        Stock s = Stock.builder()
                .symbol("STK")
                .name("Test Stock")
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals(AssetType.STOCK, s.getType());
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            Stock s = Stock.builder()
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .build();

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(0, expected.compareTo(s.getCostBasis()));
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            Stock s = Stock.builder()
                    .quantity(new BigDecimal("1.2345"))   // scale 4
                    .buyPrice(new BigDecimal("12.34"))    // scale 2
                    .build();

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(0, expected.compareTo(s.getCostBasis()));
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            Stock s = Stock.builder()
                    .quantity(null)
                    .buyPrice(new BigDecimal("100.00"))
                    .build();

            assertEquals(0, BigDecimal.ZERO.compareTo(s.getCostBasis()));
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            Stock s = Stock.builder()
                    .quantity(new BigDecimal("2"))
                    .buyPrice(null)
                    .build();

            assertEquals(0, BigDecimal.ZERO.compareTo(s.getCostBasis()));
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            Stock s = Stock.builder()
                    .quantity(new BigDecimal("-3"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(0, expected.compareTo(s.getCostBasis()));
        }
    }

    @Test
    @DisplayName("builder sets Stock-specific fields and getters return them")
    void builder_setsStockFields() {
        Stock s = Stock.builder()
                .exchange("NASDAQ")
                .sector("Technology")
                .dividendYield(new BigDecimal("1.75"))
                .marketCap("Large")
                .quantity(new BigDecimal("100"))
                .buyPrice(new BigDecimal("2.00"))
                .build();

        assertEquals("NASDAQ", s.getExchange());
        assertEquals("Technology", s.getSector());
        assertEquals("Large", s.getMarketCap());
        assertEquals(0, new BigDecimal("1.75").compareTo(s.getDividendYield()));
    }
}
