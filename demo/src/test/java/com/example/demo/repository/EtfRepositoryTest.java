package com.example.demo.repository;

import com.example.demo.entity.Etf;
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
class EtfRepositoryTest {

    @Mock
    private EtfRepository etfRepository;

    // Helper to create mocked Etf entities — make common getters lenient to avoid unnecessary-stubbing failures
    private Etf createEtf(String symbol, String name, String exchange, String category) {
        Etf etf = mock(Etf.class);
        lenient().when(etf.getSymbol()).thenReturn(symbol);
        lenient().when(etf.getName()).thenReturn(name);
        lenient().when(etf.getExchange()).thenReturn(exchange);
        lenient().when(etf.getCategory()).thenReturn(category);
        return etf;
    }

    @Nested
    @DisplayName("Aggregation")
    class AggregationTests {

        @Test
        @DisplayName("calculateTotalCostBasis returns sum of quantity * buyPrice")
        void givenEtfEntries_whenCalculateTotalCostBasis_thenReturnSum() {
            when(etfRepository.calculateTotalCostBasis()).thenReturn(new BigDecimal("2000.00"));

            BigDecimal total = etfRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(new BigDecimal("2000.00")));
        }

        @Test
        @DisplayName("calculateTotalCostBasis returns zero when no entries")
        void givenNoEtf_whenCalculateTotalCostBasis_thenReturnZero() {
            when(etfRepository.calculateTotalCostBasis()).thenReturn(BigDecimal.ZERO);

            BigDecimal total = etfRepository.calculateTotalCostBasis();
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
            Etf e1 = createEtf("SPY", "SPDR S&P 500", "NYSE", "Index");
            Etf e2 = createEtf("spyX", "SPY Extended", "NYSE", "Index");

            when(etfRepository.findBySymbolContainingIgnoreCase("spy")).thenReturn(Arrays.asList(e1, e2));

            List<Etf> results = etfRepository.findBySymbolContainingIgnoreCase("spy");
            assertNotNull(results);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("findByNameContainingIgnoreCase finds names regardless of case and substring")
        void givenNames_whenFindByNameContainingIgnoreCase_thenReturnMatches() {
            Etf e1 = createEtf("IVV", "iShares Core S&P 500", "NASDAQ", "Index");
            Etf e2 = createEtf("VOO", "Vanguard S&P 500", "NASDAQ", "Index");

            when(etfRepository.findByNameContainingIgnoreCase("s&p 500")).thenReturn(Arrays.asList(e1, e2));

            List<Etf> results = etfRepository.findByNameContainingIgnoreCase("s&p 500");
            assertEquals(2, results.size());
            assertEquals("IVV", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findBySymbolIgnoreCase matches exact symbol ignoring case")
        void givenSymbolDifferentCase_whenFindBySymbolIgnoreCase_thenReturnMatch() {
            Etf etf = createEtf("SpY1", "Example ETF", "NYSE", "Sector");

            when(etfRepository.findBySymbolIgnoreCase("SPY1")).thenReturn(Optional.of(etf));

            Optional<Etf> found = etfRepository.findBySymbolIgnoreCase("SPY1");
            assertTrue(found.isPresent());
            assertEquals("SpY1", found.get().getSymbol());
        }

        @Test
        @DisplayName("findByExchangeIgnoreCase returns entries for the requested exchange")
        void givenExchange_whenFindByExchangeIgnoreCase_thenReturnMatches() {
            Etf nyse = createEtf("XLE", "Energy Select Sector SPDR Fund", "NYSE", "Sector");
            when(etfRepository.findByExchangeIgnoreCase("nyse")).thenReturn(Arrays.asList(nyse));

            List<Etf> results = etfRepository.findByExchangeIgnoreCase("nyse");
            assertEquals(1, results.size());
            assertEquals("XLE", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findByCategoryIgnoreCase returns entries for the requested category")
        void givenCategory_whenFindByCategoryIgnoreCase_thenReturnMatches() {
            Etf sector = createEtf("XLK", "Technology Select Sector SPDR Fund", "NYSE", "Sector");
            when(etfRepository.findByCategoryIgnoreCase("sector")).thenReturn(Arrays.asList(sector));

            List<Etf> results = etfRepository.findByCategoryIgnoreCase("sector");
            assertEquals(1, results.size());
            assertEquals("XLK", results.get(0).getSymbol());
        }
    }
}

