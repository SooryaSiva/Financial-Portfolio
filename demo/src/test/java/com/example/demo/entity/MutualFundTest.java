package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MutualFundTest {

    @Test
    @DisplayName("getType returns AssetType.MUTUAL_FUND")
    void getType_returnsMutualFund() {
        MutualFund m = MutualFund.builder()
                .symbol("MF1")
                .name("Test Mutual Fund")
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals(AssetType.MUTUAL_FUND, m.getType());
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            MutualFund m = MutualFund.builder()
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .build();

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(0, expected.compareTo(m.getCostBasis()));
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            MutualFund m = MutualFund.builder()
                    .quantity(new BigDecimal("1.2345"))   // scale 4
                    .buyPrice(new BigDecimal("12.34"))    // scale 2
                    .build();

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(0, expected.compareTo(m.getCostBasis()));
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            MutualFund m = MutualFund.builder()
                    .quantity(null)
                    .buyPrice(new BigDecimal("100.00"))
                    .build();

            assertEquals(0, BigDecimal.ZERO.compareTo(m.getCostBasis()));
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            MutualFund m = MutualFund.builder()
                    .quantity(new BigDecimal("2"))
                    .buyPrice(null)
                    .build();

            assertEquals(0, BigDecimal.ZERO.compareTo(m.getCostBasis()));
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            MutualFund m = MutualFund.builder()
                    .quantity(new BigDecimal("-3"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(0, expected.compareTo(m.getCostBasis()));
        }
    }

    @Test
    @DisplayName("builder sets MutualFund-specific fields and getters return them")
    void builder_setsMutualFundFields() {
        MutualFund m = MutualFund.builder()
                .fundFamily("Vanguard")
                .expenseRatio(new BigDecimal("0.0025"))
                .category("Index")
                .minimumInvestment(new BigDecimal("1000.00"))
                .dividendYield(new BigDecimal("1.25"))
                .quantity(new BigDecimal("50"))
                .buyPrice(new BigDecimal("20.00"))
                .build();

        assertEquals("Vanguard", m.getFundFamily());
        assertEquals("Index", m.getCategory());
        assertEquals(0, new BigDecimal("0.0025").compareTo(m.getExpenseRatio()));
        assertEquals(0, new BigDecimal("1000.00").compareTo(m.getMinimumInvestment()));
        assertEquals(0, new BigDecimal("1.25").compareTo(m.getDividendYield()));
    }
}
