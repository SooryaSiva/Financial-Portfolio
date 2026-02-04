package com.example.demo.repository;

import com.example.demo.entity.RealEstate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RealEstate entities.
 */
@Repository
public interface RealEstateRepository extends BaseAssetRepository<RealEstate> {

    Optional<RealEstate> findBySymbolIgnoreCase(String symbol);

    List<RealEstate> findByPropertyType(RealEstate.PropertyType propertyType);

    List<RealEstate> findByPropertyAddressContainingIgnoreCase(String address);
}
