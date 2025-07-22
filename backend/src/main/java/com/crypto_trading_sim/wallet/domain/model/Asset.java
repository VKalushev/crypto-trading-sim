package com.crypto_trading_sim.wallet.domain.model;

import com.crypto_trading_sim.common.domain.model.BaseModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Asset extends BaseModel<Asset> {

    private String symbol;
    private BigDecimal amount;
    private UUID walletId;
    private Instant created;
    private Instant updated;

    public Asset(UUID walletId, String symbol, BigDecimal amount) {
        this.symbol = symbol;
        this.amount = amount;
        this.walletId = walletId;
    }

    public Asset() {
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

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }
}