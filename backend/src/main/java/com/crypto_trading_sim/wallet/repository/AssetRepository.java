package com.crypto_trading_sim.wallet.repository;

import com.crypto_trading_sim.wallet.domain.model.Asset;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AssetRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Asset> assetRowMapper = (rs, rowNum) -> {
        Asset asset = new Asset();
        asset.setId(UUID.fromString(rs.getString("id")));
        asset.setWalletId(UUID.fromString(rs.getString("wallet_id")));
        asset.setSymbol(rs.getString("symbol"));
        asset.setAmount(rs.getBigDecimal("amount"));
        asset.setCreated(rs.getTimestamp("created").toInstant());
        asset.setUpdated(rs.getTimestamp("updated").toInstant());
        return asset;
    };

    public List<Asset> findByWalletId(UUID walletId) {
        return jdbcTemplate.query("SELECT * FROM asset WHERE wallet_id = ?", assetRowMapper, walletId);
    }

    public Asset insert(Asset asset) {
        asset.setId(UUID.randomUUID());
        Instant now = Instant.now();
        asset.setCreated(now);
        asset.setUpdated(now);

        String sql = """
                INSERT INTO "asset" (id, wallet_id, symbol, amount, created, updated) VALUES (?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                asset.getId(),
                asset.getWalletId(),
                asset.getSymbol(),
                asset.getAmount(),
                Timestamp.from(now),
                Timestamp.from(now)
        );
        return asset;
    }

    private void update(Asset asset) {
        String sql = "UPDATE asset SET amount = ?, updated = ? WHERE id = ?";
        Instant now = Instant.now();
        asset.setUpdated(now);

        jdbcTemplate.update(sql,
                asset.getAmount(),
                Timestamp.from(now),
                asset.getId()
        );
    }

    public Optional<Asset> findByWalletIdAndSymbol(UUID walletId, String symbol) {
        String sql = "SELECT * FROM asset WHERE wallet_id = ? AND symbol = ?";
        return jdbcTemplate.query(sql, assetRowMapper, walletId, symbol)
                .stream().findFirst();
    }

    public void save(Asset asset) {
        if (asset.getId() == null) {
            insert(asset);
        } else if(asset.getAmount().compareTo(BigDecimal.ZERO) == 0){
            delete(asset.getId());
        } else {
            update(asset);
        }
    }

    public void delete(UUID id) {
        jdbcTemplate.update("DELETE FROM asset WHERE id = ?", id);
    }

    public void deleteAllByWalletId(UUID walletId) {
        jdbcTemplate.update("DELETE FROM asset WHERE wallet_id = ?", walletId);
    }
}