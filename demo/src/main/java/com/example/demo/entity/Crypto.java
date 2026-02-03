package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a cryptocurrency asset.
 */
@Entity
@Table(name = "cryptos")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Crypto extends BaseAsset {

    @Column(name = "blockchain", length = 30)
    private String blockchain; // Bitcoin, Ethereum, Solana, etc.

    @Column(name = "wallet_address", length = 100)
    private String walletAddress;

    @Column(name = "staking_enabled")
    private Boolean stakingEnabled;

    @Column(name = "staking_apy", precision = 5, scale = 2)
    private java.math.BigDecimal stakingApy; // Annual staking yield %

    @Override
    public AssetType getType() {
        return AssetType.CRYPTO;
    }
}
