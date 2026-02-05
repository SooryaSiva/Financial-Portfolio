package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CashTest {

    @Test
    @DisplayName("getType returns AssetType.CASH")
    void getType_returnsCash() {
        Cash c = Cash.builder()
                .symbol("CASH")
                .name("Cash Holding")
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals(AssetType.CASH, c.getType());
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            Cash c = Cash.builder()
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .build();

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(0, expected.compareTo(c.getCostBasis()));
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            Cash c = Cash.builder()
                    .quantity(new BigDecimal("1.2345"))   // scale 4
                    .buyPrice(new BigDecimal("12.34"))    // scale 2
                    .build();

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(0, expected.compareTo(c.getCostBasis()));
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            Cash c = Cash.builder()
                    .quantity(null)
                    .buyPrice(new BigDecimal("100.00"))
                    .build();

            assertEquals(BigDecimal.ZERO, c.getCostBasis());
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            Cash c = Cash.builder()
                    .quantity(new BigDecimal("2"))
                    .buyPrice(null)
                    .build();

            assertEquals(BigDecimal.ZERO, c.getCostBasis());
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            Cash c = Cash.builder()
                    .quantity(new BigDecimal("-3"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(0, expected.compareTo(c.getCostBasis()));
        }
    }

    @Test
    @DisplayName("builder sets Cash-specific fields and getters return them")
    void builder_setsCashFields() {
        Cash c = Cash.builder()
                .currency("USD")
                .accountType(Cash.AccountType.HIGH_YIELD_SAVINGS)
                .interestRate(new BigDecimal("1.75"))
                .bankName("TestBank")
                .quantity(new BigDecimal("100"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals("USD", c.getCurrency());
        assertEquals(Cash.AccountType.HIGH_YIELD_SAVINGS, c.getAccountType());
        assertEquals(0, new BigDecimal("1.75").compareTo(c.getInterestRate()));
        assertEquals("TestBank", c.getBankName());
    }

    @Test
    @DisplayName("AccountType enum contains expected constants and can be set")
    void accountType_enumValuesPresentAndSettable() {
        // presence checks
        assertNotNull(Cash.AccountType.valueOf("SAVINGS"));
        assertNotNull(Cash.AccountType.valueOf("CHECKING"));
        assertNotNull(Cash.AccountType.valueOf("MONEY_MARKET"));

        Cash c = Cash.builder()
                .accountType(Cash.AccountType.CD)
                .build();

        assertEquals(Cash.AccountType.CD, c.getAccountType());
    }
}
