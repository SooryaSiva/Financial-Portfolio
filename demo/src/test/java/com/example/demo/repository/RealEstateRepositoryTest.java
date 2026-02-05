package com.example.demo.repository;

import com.example.demo.entity.RealEstate;
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
class RealEstateRepositoryTest {

    @Mock
    private RealEstateRepository realEstateRepository;

    // Helper to create mocked RealEstate entities — make common getters lenient to avoid unnecessary-stubbing failures
    private RealEstate createRealEstate(String symbol, String name, RealEstate.PropertyType propertyType, String address) {
        RealEstate re = mock(RealEstate.class);
        lenient().when(re.getSymbol()).thenReturn(symbol);
        lenient().when(re.getName()).thenReturn(name);
        lenient().when(re.getPropertyType()).thenReturn(propertyType);
        lenient().when(re.getPropertyAddress()).thenReturn(address);
        return re;
    }

    @Nested
    @DisplayName("Aggregation")
    class AggregationTests {

        @Test
        @DisplayName("calculateTotalCostBasis returns sum of quantity * buyPrice")
        void givenRealEstates_whenCalculateTotalCostBasis_thenReturnSum() {
            when(realEstateRepository.calculateTotalCostBasis()).thenReturn(new BigDecimal("500000.00"));

            BigDecimal total = realEstateRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(new BigDecimal("500000.00")));
        }

        @Test
        @DisplayName("calculateTotalCostBasis returns zero when no entries")
        void givenNoRealEstate_whenCalculateTotalCostBasis_thenReturnZero() {
            when(realEstateRepository.calculateTotalCostBasis()).thenReturn(BigDecimal.ZERO);

            BigDecimal total = realEstateRepository.calculateTotalCostBasis();
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
            RealEstate.PropertyType pt = RealEstate.PropertyType.values()[0];
            RealEstate r1 = createRealEstate("PROP1", "Downtown Condo", pt, "123 Main St");
            RealEstate r2 = createRealEstate("prop1X", "Downtown Loft", pt, "125 Main St");

            when(realEstateRepository.findBySymbolContainingIgnoreCase("prop1")).thenReturn(Arrays.asList(r1, r2));

            List<RealEstate> results = realEstateRepository.findBySymbolContainingIgnoreCase("prop1");
            assertNotNull(results);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("findByNameContainingIgnoreCase finds names regardless of case and substring")
        void givenNames_whenFindByNameContainingIgnoreCase_thenReturnMatches() {
            RealEstate.PropertyType pt = RealEstate.PropertyType.values()[0];
            RealEstate r1 = createRealEstate("CON1", "Seaside Villa", pt, "1 Ocean Ave");
            RealEstate r2 = createRealEstate("CON2", "Seaside Cottage", pt, "2 Ocean Ave");

            when(realEstateRepository.findByNameContainingIgnoreCase("seaside")).thenReturn(Arrays.asList(r1, r2));

            List<RealEstate> results = realEstateRepository.findByNameContainingIgnoreCase("seaside");
            assertEquals(2, results.size());
            assertEquals("CON1", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findBySymbolIgnoreCase matches exact symbol ignoring case")
        void givenSymbolDifferentCase_whenFindBySymbolIgnoreCase_thenReturnMatch() {
            RealEstate.PropertyType pt = RealEstate.PropertyType.values()[0];
            RealEstate re = createRealEstate("ReAl1", "Example Property", pt, "10 Elm St");

            when(realEstateRepository.findBySymbolIgnoreCase("REAL1")).thenReturn(Optional.of(re));

            Optional<RealEstate> found = realEstateRepository.findBySymbolIgnoreCase("REAL1");
            assertTrue(found.isPresent());
            assertEquals("ReAl1", found.get().getSymbol());
        }

        @Test
        @DisplayName("findByPropertyType returns entries for the requested property type")
        void givenPropertyType_whenFindByPropertyType_thenReturnMatches() {
            RealEstate.PropertyType pt = RealEstate.PropertyType.values()[0];
            RealEstate house = createRealEstate("HSE1", "Family Home", pt, "50 Oak Rd");
            when(realEstateRepository.findByPropertyType(pt)).thenReturn(Arrays.asList(house));

            List<RealEstate> results = realEstateRepository.findByPropertyType(pt);
            assertEquals(1, results.size());
            assertEquals("HSE1", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findByPropertyAddressContainingIgnoreCase finds entries by address ignoring case")
        void givenAddresses_whenFindByPropertyAddressContainingIgnoreCase_thenReturnMatches() {
            RealEstate.PropertyType pt = RealEstate.PropertyType.values()[0];
            RealEstate addr = createRealEstate("ADDR1", "Corner Lot", pt, "Corner of Pine and 3rd");
            when(realEstateRepository.findByPropertyAddressContainingIgnoreCase("pine")).thenReturn(Arrays.asList(addr));

            List<RealEstate> results = realEstateRepository.findByPropertyAddressContainingIgnoreCase("pine");
            assertEquals(1, results.size());
            assertEquals("ADDR1", results.get(0).getSymbol());
        }
    }
}
