package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CryptoTest {

    @Test
    @DisplayName("getType returns AssetType.CRYPTO")
    void getType_returnsCrypto() {
        Crypto c = Crypto.builder()
                .symbol("CRY")
                .name("Test Crypto")
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals(AssetType.CRYPTO, c.getType());
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            Crypto c = Crypto.builder()
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .build();

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(0, expected.compareTo(c.getCostBasis()));
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            Crypto c = Crypto.builder()
                    .quantity(new BigDecimal("1.2345"))   // scale 4
                    .buyPrice(new BigDecimal("12.34"))    // scale 2
                    .build();

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(0, expected.compareTo(c.getCostBasis()));
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            Crypto c = Crypto.builder()
                    .quantity(null)
                    .buyPrice(new BigDecimal("100.00"))
                    .build();

            assertEquals(BigDecimal.ZERO, c.getCostBasis());
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            Crypto c = Crypto.builder()
                    .quantity(new BigDecimal("2"))
                    .buyPrice(null)
                    .build();

            assertEquals(BigDecimal.ZERO, c.getCostBasis());
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            Crypto c = Crypto.builder()
                    .quantity(new BigDecimal("-3"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(0, expected.compareTo(c.getCostBasis()));
        }
    }

    @Test
    @DisplayName("builder sets Crypto-specific fields and getters return them")
    void builder_setsCryptoFields() {
        Crypto c = Crypto.builder()
                .blockchain("Ethereum")
                .walletAddress("0xDEADBEEF")
                .stakingEnabled(Boolean.TRUE)
                .stakingApy(new BigDecimal("4.25"))
                .quantity(new BigDecimal("100"))
                .buyPrice(new BigDecimal("2.00"))
                .build();

        assertEquals("Ethereum", c.getBlockchain());
        assertEquals("0xDEADBEEF", c.getWalletAddress());
        assertTrue(Boolean.TRUE.equals(c.getStakingEnabled()));
        assertEquals(0, new BigDecimal("4.25").compareTo(c.getStakingApy()));
    }
}
