package com.crypto_trading_sim.wallet.repository;

import com.crypto_trading_sim.wallet.domain.model.Wallet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletRepository {

    private final JdbcTemplate jdbcTemplate;

    public WalletRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Wallet> walletRowMapper = (rs, rowNum) -> {
        Wallet wallet = new Wallet();
        wallet.setId(rs.getObject("id", UUID.class));
        wallet.setUserId(rs.getObject("user_id", UUID.class));
        wallet.setBalance(rs.getBigDecimal("balance"));
        return wallet;
    };

    public Optional<Wallet> findByUserId(UUID userId) {
        String sql = "SELECT * FROM wallet WHERE user_id = ?";
        return jdbcTemplate.query(sql, walletRowMapper, userId)
                .stream().findFirst();
    }

    public Wallet insert(Wallet wallet) {
        wallet.setId(UUID.randomUUID());
        String sql = "INSERT INTO wallet (id, user_id, balance) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
        return wallet;
    }

    public void update(Wallet wallet) {
        String sql = "UPDATE wallet SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, wallet.getBalance(), wallet.getId());
    }
}