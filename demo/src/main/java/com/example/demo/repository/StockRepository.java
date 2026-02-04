package com.example.demo.repository;

import com.example.demo.entity.Stock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Stock entities.
 */
@Repository
public interface StockRepository extends BaseAssetRepository<Stock> {

    Optional<Stock> findBySymbolIgnoreCase(String symbol);

    List<Stock> findByExchangeIgnoreCase(String exchange);

    List<Stock> findBySectorIgnoreCase(String sector);

    List<Stock> findByMarketCapIgnoreCase(String marketCap);
}
