package com.example.demo.repository;

import com.example.demo.entity.MutualFund;
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
class MutualFundRepositoryTest {

    @Mock
    private MutualFundRepository mutualFundRepository;

    // Helper to create mocked MutualFund entities — make common getters lenient to avoid unnecessary-stubbing failures
    private MutualFund createMutualFund(String symbol, String name, String fundFamily, String category) {
        MutualFund mf = mock(MutualFund.class);
        lenient().when(mf.getSymbol()).thenReturn(symbol);
        lenient().when(mf.getName()).thenReturn(name);
        lenient().when(mf.getFundFamily()).thenReturn(fundFamily);
        lenient().when(mf.getCategory()).thenReturn(category);
        return mf;
    }

    @Nested
    @DisplayName("Aggregation")
    class AggregationTests {

        @Test
        @DisplayName("calculateTotalCostBasis returns sum of quantity * buyPrice")
        void givenMutualFunds_whenCalculateTotalCostBasis_thenReturnSum() {
            when(mutualFundRepository.calculateTotalCostBasis()).thenReturn(new BigDecimal("1500.00"));

            BigDecimal total = mutualFundRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(new BigDecimal("1500.00")));
        }

        @Test
        @DisplayName("calculateTotalCostBasis returns zero when no entries")
        void givenNoMutualFunds_whenCalculateTotalCostBasis_thenReturnZero() {
            when(mutualFundRepository.calculateTotalCostBasis()).thenReturn(BigDecimal.ZERO);

            BigDecimal total = mutualFundRepository.calculateTotalCostBasis();
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
            MutualFund m1 = createMutualFund("VFIAX", "Vanguard 500 Index", "Vanguard", "Index");
            MutualFund m2 = createMutualFund("vfiaX", "Vanguard 500 Extended", "Vanguard", "Index");

            when(mutualFundRepository.findBySymbolContainingIgnoreCase("vfia")).thenReturn(Arrays.asList(m1, m2));

            List<MutualFund> results = mutualFundRepository.findBySymbolContainingIgnoreCase("vfia");
            assertNotNull(results);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("findByNameContainingIgnoreCase finds names regardless of case and substring")
        void givenNames_whenFindByNameContainingIgnoreCase_thenReturnMatches() {
            MutualFund m1 = createMutualFund("FXAIX", "Fidelity 500 Index Fund", "Fidelity", "Index");
            MutualFund m2 = createMutualFund("FXIAX", "Fidelity 500 Extended", "Fidelity", "Index");

            when(mutualFundRepository.findByNameContainingIgnoreCase("fidelity 500")).thenReturn(Arrays.asList(m1, m2));

            List<MutualFund> results = mutualFundRepository.findByNameContainingIgnoreCase("fidelity 500");
            assertEquals(2, results.size());
            assertEquals("FXAIX", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findBySymbolIgnoreCase matches exact symbol ignoring case")
        void givenSymbolDifferentCase_whenFindBySymbolIgnoreCase_thenReturnMatch() {
            MutualFund mf = createMutualFund("MuTuAl1", "Example MF", "FamilyA", "Sector");

            when(mutualFundRepository.findBySymbolIgnoreCase("MUTUAL1")).thenReturn(Optional.of(mf));

            Optional<MutualFund> found = mutualFundRepository.findBySymbolIgnoreCase("MUTUAL1");
            assertTrue(found.isPresent());
            assertEquals("MuTuAl1", found.get().getSymbol());
        }

        @Test
        @DisplayName("findByFundFamilyIgnoreCase returns entries for the requested fund family")
        void givenFundFamily_whenFindByFundFamilyIgnoreCase_thenReturnMatches() {
            MutualFund family = createMutualFund("MFAM1", "Family Fund", "BigFamily", "Balanced");
            when(mutualFundRepository.findByFundFamilyIgnoreCase("bigfamily")).thenReturn(Arrays.asList(family));

            List<MutualFund> results = mutualFundRepository.findByFundFamilyIgnoreCase("bigfamily");
            assertEquals(1, results.size());
            assertEquals("MFAM1", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findByCategoryIgnoreCase returns entries for the requested category")
        void givenCategory_whenFindByCategoryIgnoreCase_thenReturnMatches() {
            MutualFund category = createMutualFund("MFCAT", "Category Fund", "FamilyB", "Balanced");
            when(mutualFundRepository.findByCategoryIgnoreCase("balanced")).thenReturn(Arrays.asList(category));

            List<MutualFund> results = mutualFundRepository.findByCategoryIgnoreCase("balanced");
            assertEquals(1, results.size());
            assertEquals("MFCAT", results.get(0).getSymbol());
        }
    }
}
