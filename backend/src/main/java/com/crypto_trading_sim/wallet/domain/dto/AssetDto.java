package com.crypto_trading_sim.wallet.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AssetDto {
    private UUID id;
    private String symbol;
    private BigDecimal amount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}