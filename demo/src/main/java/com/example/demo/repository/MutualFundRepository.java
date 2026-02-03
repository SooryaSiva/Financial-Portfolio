package com.example.demo.repository;

import com.example.demo.entity.MutualFund;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for MutualFund entities.
 */
@Repository
public interface MutualFundRepository extends BaseAssetRepository<MutualFund> {

    Optional<MutualFund> findBySymbolIgnoreCase(String symbol);

    List<MutualFund> findByFundFamilyIgnoreCase(String fundFamily);

    List<MutualFund> findByCategoryIgnoreCase(String category);
}
