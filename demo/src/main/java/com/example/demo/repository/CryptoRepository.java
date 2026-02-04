package com.example.demo.repository;

import com.example.demo.entity.Crypto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Crypto entities.
 */
@Repository
public interface CryptoRepository extends BaseAssetRepository<Crypto> {

    Optional<Crypto> findBySymbolIgnoreCase(String symbol);

    List<Crypto> findByBlockchainIgnoreCase(String blockchain);

    List<Crypto> findByStakingEnabled(Boolean stakingEnabled);
}
