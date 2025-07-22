package com.crypto_trading_sim.wallet.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class WalletDto {
    private UUID id;
    private BigDecimal balance;
    private List<AssetDto> assets;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<AssetDto> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetDto> assets) {
        this.assets = assets;
    }
}