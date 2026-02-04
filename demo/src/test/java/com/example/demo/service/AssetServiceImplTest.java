package com.example.demo.service;

import com.example.demo.dto.AssetDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @InjectMocks
    private AssetServiceImpl assetService;

    @Mock
    private StockRepository stockRepository;
    @Mock
    private BondRepository bondRepository;
    @Mock
    private EtfRepository etfRepository;
    @Mock
    private MutualFundRepository mutualFundRepository;
    @Mock
    private CryptoRepository cryptoRepository;
    @Mock
    private RealEstateRepository realEstateRepository;
    @Mock
    private CashRepository cashRepository;
    @Mock
    private StockPriceService stockPriceService;

    private Stock createStockEntity(Long id, String symbol, BigDecimal qty, BigDecimal buyPrice) {
        Stock s = Stock.builder()
                .symbol(symbol)
                .name("Name " + symbol)
                .quantity(qty)
                .buyPrice(buyPrice)
                .purchaseDate(LocalDate.now())
                .build();
        s.setId(id);
        return s;
    }

    private Bond createBondEntity(Long id, String symbol, BigDecimal qty, BigDecimal buyPrice) {
        Bond b = Bond.builder()
                .symbol(symbol)
                .name("Name " + symbol)
                .quantity(qty)
                .buyPrice(buyPrice)
                .purchaseDate(LocalDate.now())
                .build();
        b.setId(id);
        return b;
    }

    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("getAllAssets")
    class GetAllAssetsTests {

        @Test
        @DisplayName("Given repositories have assets when getAllAssets then return enriched list")
        void givenReposHaveAssets_whenGetAllAssets_thenReturnEnrichedList() {
            Stock stock = createStockEntity(1L, "AAPL", new BigDecimal("2"), new BigDecimal("100"));
            when(stockRepository.findAll()).thenReturn(List.of(stock));
            when(bondRepository.findAll()).thenReturn(Collections.emptyList());
            when(etfRepository.findAll()).thenReturn(Collections.emptyList());
            when(mutualFundRepository.findAll()).thenReturn(Collections.emptyList());
            when(cryptoRepository.findAll()).thenReturn(Collections.emptyList());
            when(realEstateRepository.findAll()).thenReturn(Collections.emptyList());
            when(cashRepository.findAll()).thenReturn(Collections.emptyList());

            when(stockPriceService.getCurrentPrice("AAPL")).thenReturn(new BigDecimal("110"));

            List<AssetDTO> result = assetService.getAllAssets();

            assertEquals(1, result.size());
            AssetDTO dto = result.get(0);
            assertEquals("AAPL", dto.getSymbol());
            assertEquals(new BigDecimal("110"), dto.getCurrentPrice());
            assertNotNull(dto.getGainLoss());
        }
    }

    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("getAssetById")
    class GetAssetByIdTests {

        @Test
        @DisplayName("Given asset exists in one repository when getAssetById then return enriched DTO")
        void givenAssetExists_whenGetAssetById_thenReturnDto() {
            Long id = 10L;
            Bond bond = createBondEntity(id, "BOND1", new BigDecimal("1"), new BigDecimal("1000"));
            when(stockRepository.findById(id)).thenReturn(Optional.empty());
            when(bondRepository.findById(id)).thenReturn(Optional.of(bond));

            AssetDTO dto = assetService.getAssetById(id);

            assertEquals(id, dto.getId());
            assertEquals("BOND1", dto.getSymbol());
            assertEquals(bond.getBuyPrice(), dto.getCurrentPrice());
            assertEquals(BigDecimal.ZERO, dto.getGainLoss());
        }

        @Test
        @DisplayName("Given id not found when getAssetById then throw ResourceNotFoundException")
        void givenIdNotFound_whenGetAssetById_thenThrow() {
            Long missingId = 999L;
            when(stockRepository.findById(missingId)).thenReturn(Optional.empty());
            when(bondRepository.findById(missingId)).thenReturn(Optional.empty());
            whenEtcReposReturnEmpty(missingId);

            assertThrows(ResourceNotFoundException.class, () -> assetService.getAssetById(missingId));
        }
    }

    // helper method to stub all remaining repositories returning Optional.empty()
    private void whenEtcReposReturnEmpty(Long id) {
        when(etfRepository.findById(id)).thenReturn(Optional.empty());
        when(mutualFundRepository.findById(id)).thenReturn(Optional.empty());
        when(cryptoRepository.findById(id)).thenReturn(Optional.empty());
        when(realEstateRepository.findById(id)).thenReturn(Optional.empty());
        when(cashRepository.findById(id)).thenReturn(Optional.empty());
    }

    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("getAssetsByType")
    class GetAssetsByTypeTests {

        @Test
        @DisplayName("Given type STOCK when getAssetsByType then return only stock assets")
        void givenTypeStock_whenGetAssetsByType_thenReturnStocks() {
            Stock stock = createStockEntity(2L, "TSLA", new BigDecimal("3"), new BigDecimal("50"));
            when(stockRepository.findAll()).thenReturn(List.of(stock));

            List<AssetDTO> result = assetService.getAssetsByType(AssetType.STOCK);

            assertEquals(1, result.size());
            assertEquals("TSLA", result.get(0).getSymbol());
        }
    }

    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("searchAssets")
    class SearchAssetsTests {

        @Test
        @DisplayName("Given null query when searchAssets then return all assets")
        void givenNullQuery_whenSearchAssets_thenReturnAll() {
            Stock stock = createStockEntity(3L, "GOOG", new BigDecimal("1"), new BigDecimal("200"));
            when(stockRepository.findAll()).thenReturn(List.of(stock));
            when(bondRepository.findAll()).thenReturn(Collections.emptyList());
            when(etfRepository.findAll()).thenReturn(Collections.emptyList());
            when(mutualFundRepository.findAll()).thenReturn(Collections.emptyList());
            when(cryptoRepository.findAll()).thenReturn(Collections.emptyList());
            when(realEstateRepository.findAll()).thenReturn(Collections.emptyList());
            when(cashRepository.findAll()).thenReturn(Collections.emptyList());

            List<AssetDTO> result = assetService.searchAssets(null);

            assertEquals(1, result.size());
            assertEquals("GOOG", result.get(0).getSymbol());
        }

        @Test
        @DisplayName("Given query when searchAssets then combine multiple repo results")
        void givenQuery_whenSearchAssets_thenCombineResults() {
            String q = "part";
            Stock s = createStockEntity(4L, "PART", new BigDecimal("1"), new BigDecimal("10"));
            when(stockRepository.findBySymbolContainingIgnoreCase(q)).thenReturn(List.of(s));
            when(stockRepository.findByNameContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(bondRepository.findBySymbolContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(bondRepository.findByNameContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(etfRepository.findBySymbolContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(etfRepository.findByNameContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(mutualFundRepository.findBySymbolContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(mutualFundRepository.findByNameContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(cryptoRepository.findBySymbolContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(cryptoRepository.findByNameContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(realEstateRepository.findBySymbolContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(realEstateRepository.findByNameContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(cashRepository.findBySymbolContainingIgnoreCase(q)).thenReturn(Collections.emptyList());
            when(cashRepository.findByNameContainingIgnoreCase(q)).thenReturn(Collections.emptyList());

            when(stockPriceService.getCurrentPrice("PART")).thenReturn(new BigDecimal("12"));

            List<AssetDTO> result = assetService.searchAssets(q);

            assertEquals(1, result.size());
            assertEquals("PART", result.get(0).getSymbol());
            assertEquals(new BigDecimal("12"), result.get(0).getCurrentPrice());
        }
    }

    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("createAsset")
    class CreateAssetTests {

        @Test
        @DisplayName("Given valid stock DTO when createAsset then save and return enriched DTO")
        void givenValidStockDto_whenCreateAsset_thenSaveAndReturn() {
            AssetDTO dto = AssetDTO.builder()
                    .symbol("nvda")
                    .name("NVDA Corp")
                    .type(AssetType.STOCK)
                    .quantity(new BigDecimal("5"))
                    .buyPrice(new BigDecimal("20"))
                    .purchaseDate(LocalDate.now())
                    .build();

            Stock saved = createStockEntity(11L, "NVDA", dto.getQuantity(), dto.getBuyPrice());
            when(stockRepository.save(any(Stock.class))).thenReturn(saved);
            when(stockPriceService.getCurrentPrice("NVDA")).thenReturn(new BigDecimal("25"));

            AssetDTO result = assetService.createAsset(dto);

            assertEquals(11L, result.getId());
            assertEquals("NVDA", result.getSymbol());
            assertEquals(new BigDecimal("25"), result.getCurrentPrice());
        }
    }

    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("updateAsset")
    class UpdateAssetTests {

        @Test
        @DisplayName("Given existing asset when updateAsset then persist changes and return DTO")
        void givenExistingAsset_whenUpdateAsset_thenPersistAndReturn() {
            Long id = 20L;
            Stock existing = createStockEntity(id, "old", new BigDecimal("2"), new BigDecimal("10"));
            when(stockRepository.findById(id)).thenReturn(Optional.of(existing));

            Stock updatedEntity = createStockEntity(id, "NEW", new BigDecimal("3"), new BigDecimal("15"));
            when(stockRepository.save(any(Stock.class))).thenReturn(updatedEntity);
            when(stockPriceService.getCurrentPrice("NEW")).thenReturn(new BigDecimal("16"));

            AssetDTO updateDto = AssetDTO.builder()
                    .symbol("new")
                    .name("New Name")
                    .quantity(new BigDecimal("3"))
                    .buyPrice(new BigDecimal("15"))
                    .purchaseDate(LocalDate.now())
                    .build();

            AssetDTO result = assetService.updateAsset(id, updateDto);

            assertEquals(id, result.getId());
            assertEquals("NEW", result.getSymbol());
            assertEquals(new BigDecimal("16"), result.getCurrentPrice());
        }

        @Test
        @DisplayName("Given non-existing id when updateAsset then throw ResourceNotFoundException")
        void givenNonExistingId_whenUpdateAsset_thenThrow() {
            Long missing = 9999L;
            when(stockRepository.findById(missing)).thenReturn(Optional.empty());
            when(bondRepository.findById(missing)).thenReturn(Optional.empty());
            when(etfRepository.findById(missing)).thenReturn(Optional.empty());
            when(mutualFundRepository.findById(missing)).thenReturn(Optional.empty());
            when(cryptoRepository.findById(missing)).thenReturn(Optional.empty());
            when(realEstateRepository.findById(missing)).thenReturn(Optional.empty());
            when(cashRepository.findById(missing)).thenReturn(Optional.empty());

            AssetDTO dto = AssetDTO.builder().symbol("x").type(AssetType.STOCK).quantity(BigDecimal.ONE).buyPrice(BigDecimal.ONE).build();

            assertThrows(ResourceNotFoundException.class, () -> assetService.updateAsset(missing, dto));
        }
    }

    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("deleteAsset")
    class DeleteAssetTests {

        @Test
        @DisplayName("Given existing asset when deleteAsset then remove from repository")
        void givenExistingAsset_whenDeleteAsset_thenRemove() {
            Long id = 30L;
            Stock stock = createStockEntity(id, "DEL", new BigDecimal("1"), new BigDecimal("5"));
            when(stockRepository.findById(id)).thenReturn(Optional.of(stock));

            assetService.deleteAsset(id);

            verify(stockRepository).delete(stock);
        }

        @Test
        @DisplayName("Given missing asset when deleteAsset then throw ResourceNotFoundException")
        void givenMissing_whenDeleteAsset_thenThrow() {
            Long missing = 12345L;
            when(stockRepository.findById(missing)).thenReturn(Optional.empty());
            when(bondRepository.findById(missing)).thenReturn(Optional.empty());
            when(etfRepository.findById(missing)).thenReturn(Optional.empty());
            when(mutualFundRepository.findById(missing)).thenReturn(Optional.empty());
            when(cryptoRepository.findById(missing)).thenReturn(Optional.empty());
            when(realEstateRepository.findById(missing)).thenReturn(Optional.empty());
            when(cashRepository.findById(missing)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> assetService.deleteAsset(missing));
        }
    }
}
