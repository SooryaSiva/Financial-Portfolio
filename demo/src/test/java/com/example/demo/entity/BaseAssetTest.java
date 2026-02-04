package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BaseAssetTest {

    // Simple concrete subclass for testing the abstract BaseAsset
    static class ConcreteAsset extends BaseAsset {
        public ConcreteAsset() {
            super();
        }

        @Override
        public AssetType getType() {
            return AssetType.STOCK;
        }
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            ConcreteAsset a = new ConcreteAsset();
            a.setQuantity(new BigDecimal("10"));
            a.setBuyPrice(new BigDecimal("150.00"));

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(expected, a.getCostBasis());
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            ConcreteAsset a = new ConcreteAsset();
            a.setQuantity(new BigDecimal("1.2345"));   // scale 4
            a.setBuyPrice(new BigDecimal("12.34"));    // scale 2

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(expected, a.getCostBasis());
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            ConcreteAsset a = new ConcreteAsset();
            a.setQuantity(null);
            a.setBuyPrice(new BigDecimal("100.00"));

            assertEquals(BigDecimal.ZERO, a.getCostBasis());
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            ConcreteAsset a = new ConcreteAsset();
            a.setQuantity(new BigDecimal("2"));
            a.setBuyPrice(null);

            assertEquals(BigDecimal.ZERO, a.getCostBasis());
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            ConcreteAsset a = new ConcreteAsset();
            a.setQuantity(new BigDecimal("-3"));
            a.setBuyPrice(new BigDecimal("10.00"));

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(expected, a.getCostBasis());
        }
    }

    @Test
    @DisplayName("getType is implemented by concrete subclass")
    void getType_returnsConcreteType() {
        ConcreteAsset a = new ConcreteAsset();
        assertEquals(AssetType.STOCK, a.getType());
    }
}
