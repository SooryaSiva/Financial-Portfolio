package com.example.demo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RealEstateTest {

    @Test
    @DisplayName("getType returns AssetType.REAL_ESTATE")
    void getType_returnsRealEstate() {
        RealEstate r = RealEstate.builder()
                .symbol("RE1")
                .name("Test Real Estate")
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("1.00"))
                .build();

        assertEquals(AssetType.REAL_ESTATE, r.getType());
    }

    @Nested
    @DisplayName("getCostBasis behavior")
    class CostBasisTests {

        @Test
        @DisplayName("quantity and buyPrice present -> returns product with expected scale")
        void costBasis_whenQuantityAndBuyPricePresent_returnsProduct() {
            RealEstate r = RealEstate.builder()
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150.00"))
                    .build();

            BigDecimal expected = new BigDecimal("1500.00");
            assertEquals(0, expected.compareTo(r.getCostBasis()));
        }

        @Test
        @DisplayName("decimal quantity and price -> preserves expected precision (scale = sum of scales)")
        void costBasis_withDecimalValues_returnsPreciseProduct() {
            RealEstate r = RealEstate.builder()
                    .quantity(new BigDecimal("1.2345"))   // scale 4
                    .buyPrice(new BigDecimal("12.34"))    // scale 2
                    .build();

            BigDecimal expected = new BigDecimal("15.233730"); // scale 6 (4 + 2)
            assertEquals(0, expected.compareTo(r.getCostBasis()));
        }

        @Test
        @DisplayName("null quantity -> returns zero")
        void costBasis_whenQuantityNull_returnsZero() {
            RealEstate r = RealEstate.builder()
                    .quantity(null)
                    .buyPrice(new BigDecimal("100.00"))
                    .build();

            assertEquals(0, BigDecimal.ZERO.compareTo(r.getCostBasis()));
        }

        @Test
        @DisplayName("null buyPrice -> returns zero")
        void costBasis_whenBuyPriceNull_returnsZero() {
            RealEstate r = RealEstate.builder()
                    .quantity(new BigDecimal("2"))
                    .buyPrice(null)
                    .build();

            assertEquals(0, BigDecimal.ZERO.compareTo(r.getCostBasis()));
        }

        @Test
        @DisplayName("negative quantity -> returns negative product")
        void costBasis_whenNegativeQuantity_returnsNegativeProduct() {
            RealEstate r = RealEstate.builder()
                    .quantity(new BigDecimal("-3"))
                    .buyPrice(new BigDecimal("10.00"))
                    .build();

            BigDecimal expected = new BigDecimal("-30.00");
            assertEquals(0, expected.compareTo(r.getCostBasis()));
        }
    }

    @Test
    @DisplayName("builder sets RealEstate-specific fields and enum can be set/read")
    void builder_setsRealEstateFields() {
        RealEstate r = RealEstate.builder()
                .propertyAddress("123 Main St")
                .propertyType(RealEstate.PropertyType.COMMERCIAL)
                .rentalIncome(new BigDecimal("2500.00"))
                .squareFootage(2000)
                .yearBuilt(1995)
                .quantity(new BigDecimal("1"))
                .buyPrice(new BigDecimal("100000.00"))
                .build();

        assertEquals("123 Main St", r.getPropertyAddress());
        assertEquals(RealEstate.PropertyType.COMMERCIAL, r.getPropertyType());
        assertEquals(0, new BigDecimal("2500.00").compareTo(r.getRentalIncome()));
        assertEquals(Integer.valueOf(2000), r.getSquareFootage());
        assertEquals(Integer.valueOf(1995), r.getYearBuilt());
    }

    @Test
    @DisplayName("PropertyType enum contains expected constants")
    void propertyType_enumValuesPresent() {
        assertNotNull(RealEstate.PropertyType.valueOf("RESIDENTIAL"));
        assertNotNull(RealEstate.PropertyType.valueOf("COMMERCIAL"));
        assertNotNull(RealEstate.PropertyType.valueOf("REIT"));
    }
}
