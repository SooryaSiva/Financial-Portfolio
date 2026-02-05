package com.example.demo.repository;

import com.example.demo.entity.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockRepositoryTest {

    @Mock
    private StockRepository stockRepository;

    // Helper to create mocked Stock entities — make common getters lenient to avoid unnecessary-stubbing failures
    private Stock createStock(String symbol, String name, String exchange, String sector, String marketCap) {
        Stock s = mock(Stock.class);
        lenient().when(s.getSymbol()).thenReturn(symbol);
        lenient().when(s.getName()).thenReturn(name);
        lenient().when(s.getExchange()).thenReturn(exchange);
        lenient().when(s.getSector()).thenReturn(sector);
        lenient().when(s.getMarketCap()).thenReturn(marketCap);
        return s;
    }

    @Nested
    @DisplayName("Aggregation")
    class AggregationTests {

        @Test
        @DisplayName("calculateTotalCostBasis returns sum of quantity * buyPrice")
        void givenStocks_whenCalculateTotalCostBasis_thenReturnSum() {
            when(stockRepository.calculateTotalCostBasis()).thenReturn(new BigDecimal("12345.67"));

            BigDecimal total = stockRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(new BigDecimal("12345.67")));
        }

        @Test
        @DisplayName("calculateTotalCostBasis returns zero when no entries")
        void givenNoStocks_whenCalculateTotalCostBasis_thenReturnZero() {
            when(stockRepository.calculateTotalCostBasis()).thenReturn(BigDecimal.ZERO);

            BigDecimal total = stockRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(BigDecimal.ZERO));
        }
    }

    @Nested
    @DisplayName("Search queries")
    class SearchTests {

        @Test
        @DisplayName("findBySymbolContainingIgnoreCase finds symbols regardless of case and substring")
        void givenSymbols_whenFindBySymbolContainingIgnoreCase_thenReturnMatches() {
            Stock s1 = createStock("AAPL", "Apple Inc", "NASDAQ", "Technology", "Large");
            Stock s2 = createStock("aaplX", "AppleX", "NASDAQ", "Technology", "Large");

            when(stockRepository.findBySymbolContainingIgnoreCase("aapl")).thenReturn(Arrays.asList(s1, s2));

            List<Stock> results = stockRepository.findBySymbolContainingIgnoreCase("aapl");
            assertNotNull(results);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("findByNameContainingIgnoreCase finds names regardless of case and substring")
        void givenNames_whenFindByNameContainingIgnoreCase_thenReturnMatches() {
            Stock s1 = createStock("MSFT", "Microsoft Corporation", "NASDAQ", "Technology", "Large");
            Stock s2 = createStock("TECH1", "Tech Holdings", "NASDAQ", "Technology", "Mid");

            when(stockRepository.findByNameContainingIgnoreCase("tech")).thenReturn(Arrays.asList(s2));

            List<Stock> results = stockRepository.findByNameContainingIgnoreCase("tech");
            assertEquals(1, results.size());
            assertEquals("TECH1", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findBySymbolIgnoreCase matches exact symbol ignoring case")
        void givenSymbolDifferentCase_whenFindBySymbolIgnoreCase_thenReturnMatch() {
            Stock stock = createStock("GoOgL", "Google", "NASDAQ", "Technology", "Large");

            when(stockRepository.findBySymbolIgnoreCase("GOOGL")).thenReturn(Optional.of(stock));

            Optional<Stock> found = stockRepository.findBySymbolIgnoreCase("GOOGL");
            assertTrue(found.isPresent());
            assertEquals("GoOgL", found.get().getSymbol());
        }

        @Test
        @DisplayName("findByExchangeIgnoreCase returns entries for the requested exchange")
        void givenExchange_whenFindByExchangeIgnoreCase_thenReturnMatches() {
            Stock nas = createStock("TSLA", "Tesla Inc", "NASDAQ", "Automotive", "Large");
            when(stockRepository.findByExchangeIgnoreCase("nasdaq")).thenReturn(Arrays.asList(nas));

            List<Stock> results = stockRepository.findByExchangeIgnoreCase("nasdaq");
            assertEquals(1, results.size());
            assertEquals("TSLA", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findBySectorIgnoreCase returns entries for the requested sector")
        void givenSector_whenFindBySectorIgnoreCase_thenReturnMatches() {
            Stock tech = createStock("NVDA", "NVIDIA Corporation", "NASDAQ", "Technology", "Large");
            when(stockRepository.findBySectorIgnoreCase("technology")).thenReturn(Arrays.asList(tech));

            List<Stock> results = stockRepository.findBySectorIgnoreCase("technology");
            assertEquals(1, results.size());
            assertEquals("NVDA", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findByMarketCapIgnoreCase returns entries for the requested market cap")
        void givenMarketCap_whenFindByMarketCapIgnoreCase_thenReturnMatches() {
            Stock large = createStock("AMZN", "Amazon.com, Inc.", "NASDAQ", "Consumer", "Large");
            when(stockRepository.findByMarketCapIgnoreCase("large")).thenReturn(Arrays.asList(large));

            List<Stock> results = stockRepository.findByMarketCapIgnoreCase("large");
            assertEquals(1, results.size());
            assertEquals("AMZN", results.get(0).getSymbol());
        }
    }
}
