package com.crypto_trading_sim.transaction.repository;

import com.crypto_trading_sim.transaction.domain.model.Transaction;
import com.crypto_trading_sim.transaction.domain.model.TransactionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getObject("id", UUID.class));
        transaction.setWalletId(rs.getObject("wallet_id", UUID.class));
        transaction.setSymbol(rs.getString("symbol"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setPrice(rs.getBigDecimal("price"));
        transaction.setType(TransactionType.valueOf(rs.getString("type")));
        transaction.setCreated(rs.getTimestamp("created").toInstant());
        return transaction;
    };

    public Transaction insert(Transaction transaction) {
        transaction.setId(UUID.randomUUID());
        transaction.setCreated(Instant.now());
        String sql = "INSERT INTO transaction (id, wallet_id, symbol, amount, price, type, created) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                transaction.getId(),
                transaction.getWalletId(),
                transaction.getSymbol(),
                transaction.getAmount(),
                transaction.getPrice(),
                transaction.getType().name(),
                Timestamp.from(transaction.getCreated())
        );
        return transaction;
    }

    public List<Transaction> findAllByWalletId(UUID walletId) {
        return jdbcTemplate.query("SELECT * FROM transaction WHERE wallet_id = ?", transactionRowMapper, walletId);
    }
}