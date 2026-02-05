package com.example.demo.dto;

import com.example.demo.entity.AssetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PortfolioSummaryDTOTest {

    @Nested
    @DisplayName("builder and accessors")
    class BuilderAndAccessors {
        @Test
        @DisplayName("Given values when built then getters return same values")
        void givenValues_whenBuild_thenAccessorsWork() {
            AssetDTO a1 = AssetDTO.builder()
                    .id(1L)
                    .symbol("AAPL")
                    .name("Apple")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("10"))
                    .buyPrice(new BigDecimal("150"))
                    .build();

            AssetDTO a2 = AssetDTO.builder()
                    .id(2L)
                    .symbol("BTC")
                    .name("Bitcoin")
                    .type(AssetType.CRYPTO)
                    .quantity(new BigDecimal("0.5"))
                    .buyPrice(new BigDecimal("30000"))
                    .build();

            PortfolioSummaryDTO dto = PortfolioSummaryDTO.builder()
                    .totalValue(new BigDecimal("50000"))
                    .totalCostBasis(new BigDecimal("48000"))
                    .totalGainLoss(new BigDecimal("2000"))
                    .totalGainLossPercentage(new BigDecimal("4.17"))
                    .totalAssets(2L)
                    .assetCountByType(Map.of("STOCK", 1L, "CRYPTO", 1L))
                    .allocationByType(Map.of("STOCK", new BigDecimal("60"), "CRYPTO", new BigDecimal("40")))
                    .valueByType(Map.of("STOCK", new BigDecimal("30000"), "CRYPTO", new BigDecimal("20000")))
                    .assets(List.of(a1, a2))
                    .topGainers(List.of(a1))
                    .topLosers(List.of(a2))
                    .build();

            assertEquals(new BigDecimal("50000"), dto.getTotalValue());
            assertEquals(new BigDecimal("48000"), dto.getTotalCostBasis());
            assertEquals(new BigDecimal("2000"), dto.getTotalGainLoss());
            assertEquals(2L, dto.getTotalAssets());
            assertEquals(1L, dto.getAssetCountByType().get("STOCK"));
            assertEquals(new BigDecimal("60"), dto.getAllocationByType().get("STOCK"));
            assertEquals(2, dto.getAssets().size());
            assertEquals("AAPL", dto.getAssets().get(0).getSymbol());
            assertEquals("BTC", dto.getAssets().get(1).getSymbol());
            assertEquals(1, dto.getTopGainers().size());
            assertEquals(1, dto.getTopLosers().size());
        }
    }

    @Nested
    @DisplayName("collections handling")
    class CollectionsHandling {
        @Test
        @DisplayName("Empty lists and maps are preserved")
        void emptyCollections_preserved() {
            PortfolioSummaryDTO dto = PortfolioSummaryDTO.builder()
                    .assets(List.of())
                    .topGainers(List.of())
                    .topLosers(List.of())
                    .assetCountByType(Map.of())
                    .allocationByType(Map.of())
                    .valueByType(Map.of())
                    .build();

            assertNotNull(dto.getAssets());
            assertTrue(dto.getAssets().isEmpty());
            assertNotNull(dto.getTopGainers());
            assertTrue(dto.getTopGainers().isEmpty());
            assertNotNull(dto.getAssetCountByType());
            assertTrue(dto.getAssetCountByType().isEmpty());
        }
    }

    @Nested
    @DisplayName("nullability")
    class Nullability {
        @Test
        @DisplayName("Fields may be null and getters return null")
        void nullFields_allowed() {
            PortfolioSummaryDTO dto = new PortfolioSummaryDTO();
            assertNull(dto.getTotalValue());
            assertNull(dto.getAssets());
            assertNull(dto.getAllocationByType());
        }
    }
}
