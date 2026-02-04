package com.example.demo.repository;

import com.example.demo.entity.Etf;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ETF entities.
 */
@Repository
public interface EtfRepository extends BaseAssetRepository<Etf> {

    Optional<Etf> findBySymbolIgnoreCase(String symbol);

    List<Etf> findByExchangeIgnoreCase(String exchange);

    List<Etf> findByCategoryIgnoreCase(String category);
}
