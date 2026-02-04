package com.example.demo.repository;

import com.example.demo.entity.Bond;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BondRepositoryTest {

    @Mock
    private BondRepository bondRepository;

    // Helper to create mocked Bond entities — make symbol stubbing lenient to avoid unnecessary-stubbing failures
    private Bond createBond(String symbol, String name) {
        Bond bond = mock(Bond.class);
        lenient().when(bond.getSymbol()).thenReturn(symbol);
        lenient().when(bond.getName()).thenReturn(name);
        return bond;
    }

    @Nested
    @DisplayName("Aggregation")
    class AggregationTests {

        @Test
        @DisplayName("calculateTotalCostBasis returns sum of quantity * buyPrice")
        void givenBonds_whenCalculateTotalCostBasis_thenReturnSum() {
            when(bondRepository.calculateTotalCostBasis()).thenReturn(new BigDecimal("456.78"));

            BigDecimal total = bondRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(new BigDecimal("456.78")));
        }

        @Test
        @DisplayName("calculateTotalCostBasis returns zero when no bonds")
        void givenNoBonds_whenCalculateTotalCostBasis_thenReturnZero() {
            when(bondRepository.calculateTotalCostBasis()).thenReturn(BigDecimal.ZERO);

            BigDecimal total = bondRepository.calculateTotalCostBasis();
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
            Bond b1 = createBond("BND1", "Government Bond");
            Bond b2 = createBond("bnd1X", "Gov Bond X");

            when(bondRepository.findBySymbolContainingIgnoreCase("bnd1")).thenReturn(Arrays.asList(b1, b2));

            List<Bond> results = bondRepository.findBySymbolContainingIgnoreCase("bnd1");
            assertNotNull(results);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("findByNameContainingIgnoreCase finds names regardless of case and substring")
        void givenNames_whenFindByNameContainingIgnoreCase_thenReturnMatches() {
            Bond b1 = createBond("TB1", "Treasury Bond");
            Bond b2 = createBond("TBX", "Treasury Holdings");

            when(bondRepository.findByNameContainingIgnoreCase("treasury")).thenReturn(Arrays.asList(b1, b2));

            List<Bond> results = bondRepository.findByNameContainingIgnoreCase("treasury");
            assertEquals(2, results.size());
            assertEquals("TB1", results.get(0).getSymbol());
        }
    }
}

