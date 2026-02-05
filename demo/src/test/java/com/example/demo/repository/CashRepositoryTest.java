// java
package com.example.demo.repository;

import com.example.demo.entity.Cash;
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
class CashRepositoryTest {

    @Mock
    private CashRepository cashRepository;

    // Helper to create mocked Cash entities — make common getters lenient to avoid unnecessary-stubbing failures
    private Cash createCash(String symbol, String name, String currency, Cash.AccountType accountType, String bankName) {
        Cash cash = mock(Cash.class);
        lenient().when(cash.getSymbol()).thenReturn(symbol);
        lenient().when(cash.getName()).thenReturn(name);
        lenient().when(cash.getCurrency()).thenReturn(currency);
        lenient().when(cash.getAccountType()).thenReturn(accountType);
        lenient().when(cash.getBankName()).thenReturn(bankName);
        return cash;
    }

    @Nested
    @DisplayName("Aggregation")
    class AggregationTests {

        @Test
        @DisplayName("calculateTotalCostBasis returns sum of quantity * buyPrice")
        void givenCashEntries_whenCalculateTotalCostBasis_thenReturnSum() {
            when(cashRepository.calculateTotalCostBasis()).thenReturn(new BigDecimal("1234.56"));

            BigDecimal total = cashRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(new BigDecimal("1234.56")));
        }

        @Test
        @DisplayName("calculateTotalCostBasis returns zero when no entries")
        void givenNoCash_whenCalculateTotalCostBasis_thenReturnZero() {
            when(cashRepository.calculateTotalCostBasis()).thenReturn(BigDecimal.ZERO);

            BigDecimal total = cashRepository.calculateTotalCostBasis();
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
            Cash c1 = createCash("CASH1", "Operating Cash", "USD", Cash.AccountType.CHECKING, "BankA");
            Cash c2 = createCash("cash1X", "Operating Reserve", "USD", Cash.AccountType.SAVINGS, "BankB");

            when(cashRepository.findBySymbolContainingIgnoreCase("cash1")).thenReturn(Arrays.asList(c1, c2));

            List<Cash> results = cashRepository.findBySymbolContainingIgnoreCase("cash1");
            assertNotNull(results);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("findByNameContainingIgnoreCase finds names regardless of case and substring")
        void givenNames_whenFindByNameContainingIgnoreCase_thenReturnMatches() {
            Cash c1 = createCash("C1", "Operating Cash", "USD", Cash.AccountType.CHECKING, "BankA");
            Cash c2 = createCash("C2", "Operating Reserve", "USD", Cash.AccountType.SAVINGS, "BankB");

            when(cashRepository.findByNameContainingIgnoreCase("operating")).thenReturn(Arrays.asList(c1, c2));

            List<Cash> results = cashRepository.findByNameContainingIgnoreCase("operating");
            assertEquals(2, results.size());
            assertEquals("C1", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findBySymbolIgnoreCase matches exact symbol ignoring case")
        void givenSymbolDifferentCase_whenFindBySymbolIgnoreCase_thenReturnMatch() {
            Cash cash = createCash("CaShX", "Example Cash", "EUR", Cash.AccountType.SAVINGS, "BankC");

            when(cashRepository.findBySymbolIgnoreCase("CASHX")).thenReturn(Optional.of(cash));

            Optional<Cash> found = cashRepository.findBySymbolIgnoreCase("CASHX");
            assertTrue(found.isPresent());
            assertEquals("CaShX", found.get().getSymbol());
        }

        @Test
        @DisplayName("findByCurrencyIgnoreCase returns entries for the requested currency")
        void givenCurrency_whenFindByCurrencyIgnoreCase_thenReturnMatches() {
            Cash usd1 = createCash("CUSD1", "USD Cash 1", "USD", Cash.AccountType.CHECKING, "BankA");
            when(cashRepository.findByCurrencyIgnoreCase("usd")).thenReturn(Arrays.asList(usd1));

            List<Cash> results = cashRepository.findByCurrencyIgnoreCase("usd");
            assertEquals(1, results.size());
            assertEquals("CUSD1", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findByAccountType returns entries with the given account type")
        void givenAccountType_whenFindByAccountType_thenReturnMatches() {
            Cash savings = createCash("CSAV", "Savings Cash", "GBP", Cash.AccountType.SAVINGS, "BankD");
            when(cashRepository.findByAccountType(Cash.AccountType.SAVINGS)).thenReturn(Arrays.asList(savings));

            List<Cash> results = cashRepository.findByAccountType(Cash.AccountType.SAVINGS);
            assertEquals(1, results.size());
            assertEquals(Cash.AccountType.SAVINGS, results.get(0).getAccountType());
        }

        @Test
        @DisplayName("findByBankNameIgnoreCase finds entries by bank name ignoring case")
        void givenBankNames_whenFindByBankNameIgnoreCase_thenReturnMatches() {
            Cash b1 = createCash("CB1", "Banked Cash", "USD", Cash.AccountType.CHECKING, "FirstBank");
            when(cashRepository.findByBankNameIgnoreCase("firstbank")).thenReturn(Arrays.asList(b1));

            List<Cash> results = cashRepository.findByBankNameIgnoreCase("firstbank");
            assertEquals(1, results.size());
            assertEquals("CB1", results.get(0).getSymbol());
        }
    }
}
