package com.example.demo.repository;

import com.example.demo.entity.Crypto;
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
class CryptoRepositoryTest {

    @Mock
    private CryptoRepository cryptoRepository;

    // Helper to create mocked Crypto entities — make common getters lenient to avoid unnecessary-stubbing failures
    private Crypto createCrypto(String symbol, String name, String blockchain, Boolean stakingEnabled) {
        Crypto crypto = mock(Crypto.class);
        lenient().when(crypto.getSymbol()).thenReturn(symbol);
        lenient().when(crypto.getName()).thenReturn(name);
        lenient().when(crypto.getBlockchain()).thenReturn(blockchain);
        lenient().when(crypto.getStakingEnabled()).thenReturn(stakingEnabled);
        return crypto;
    }

    @Nested
    @DisplayName("Aggregation")
    class AggregationTests {

        @Test
        @DisplayName("calculateTotalCostBasis returns sum of quantity * buyPrice")
        void givenCryptoEntries_whenCalculateTotalCostBasis_thenReturnSum() {
            when(cryptoRepository.calculateTotalCostBasis()).thenReturn(new BigDecimal("9876.54"));

            BigDecimal total = cryptoRepository.calculateTotalCostBasis();
            assertNotNull(total);
            assertEquals(0, total.compareTo(new BigDecimal("9876.54")));
        }

        @Test
        @DisplayName("calculateTotalCostBasis returns zero when no entries")
        void givenNoCrypto_whenCalculateTotalCostBasis_thenReturnZero() {
            when(cryptoRepository.calculateTotalCostBasis()).thenReturn(BigDecimal.ZERO);

            BigDecimal total = cryptoRepository.calculateTotalCostBasis();
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
            Crypto c1 = createCrypto("BTC", "Bitcoin", "Bitcoin", false);
            Crypto c2 = createCrypto("btcX", "BitcoinX", "Bitcoin", true);

            when(cryptoRepository.findBySymbolContainingIgnoreCase("btc")).thenReturn(Arrays.asList(c1, c2));

            List<Crypto> results = cryptoRepository.findBySymbolContainingIgnoreCase("btc");
            assertNotNull(results);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("findByNameContainingIgnoreCase finds names regardless of case and substring")
        void givenNames_whenFindByNameContainingIgnoreCase_thenReturnMatches() {
            Crypto c1 = createCrypto("ETH", "Ethereum", "Ethereum", false);
            Crypto c2 = createCrypto("ETHX", "EthereumX", "Ethereum", true);

            when(cryptoRepository.findByNameContainingIgnoreCase("ethereum")).thenReturn(Arrays.asList(c1, c2));

            List<Crypto> results = cryptoRepository.findByNameContainingIgnoreCase("ethereum");
            assertEquals(2, results.size());
            assertEquals("ETH", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findBySymbolIgnoreCase matches exact symbol ignoring case")
        void givenSymbolDifferentCase_whenFindBySymbolIgnoreCase_thenReturnMatch() {
            Crypto crypto = createCrypto("BiTcOin", "Bitcoin Example", "Bitcoin", false);

            when(cryptoRepository.findBySymbolIgnoreCase("BITCOIN")).thenReturn(Optional.of(crypto));

            Optional<Crypto> found = cryptoRepository.findBySymbolIgnoreCase("BITCOIN");
            assertTrue(found.isPresent());
            assertEquals("BiTcOin", found.get().getSymbol());
        }

        @Test
        @DisplayName("findByBlockchainIgnoreCase returns entries for the requested blockchain")
        void givenBlockchain_whenFindByBlockchainIgnoreCase_thenReturnMatches() {
            Crypto eth = createCrypto("ETH", "Ethereum", "Ethereum", true);
            when(cryptoRepository.findByBlockchainIgnoreCase("ethereum")).thenReturn(Arrays.asList(eth));

            List<Crypto> results = cryptoRepository.findByBlockchainIgnoreCase("ethereum");
            assertEquals(1, results.size());
            assertEquals("ETH", results.get(0).getSymbol());
        }

        @Test
        @DisplayName("findByStakingEnabled returns entries with the given staking flag")
        void givenStakingFlag_whenFindByStakingEnabled_thenReturnMatches() {
            Crypto staking = createCrypto("SOL", "Solana", "Solana", true);
            when(cryptoRepository.findByStakingEnabled(Boolean.TRUE)).thenReturn(Arrays.asList(staking));

            List<Crypto> results = cryptoRepository.findByStakingEnabled(Boolean.TRUE);
            assertEquals(1, results.size());
            assertTrue(results.get(0).getStakingEnabled());
        }
    }
}
