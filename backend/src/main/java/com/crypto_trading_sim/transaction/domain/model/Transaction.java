package com.crypto_trading_sim.transaction.domain.model;

import com.crypto_trading_sim.common.domain.model.BaseModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction extends BaseModel<Transaction> {
    private UUID walletId;
    private String symbol;
    private BigDecimal amount;
    private BigDecimal price;
    private TransactionType type;
    private Instant created;

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}