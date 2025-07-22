package com.crypto_trading_sim.user.repository;

import com.crypto_trading_sim.user.domain.model.AppRole;
import com.crypto_trading_sim.user.domain.model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id"))); // assuming BaseModel has `id`
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(AppRole.valueOf(rs.getString("role")));
        return user;
    };

    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT * FROM "user" u WHERE u.username = ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, username)
                .stream().findFirst();
    }

    public User insert(User user) {
        UUID id = UUID.randomUUID();
        Instant created = Instant.now();
        Timestamp createdTs = Timestamp.from(created);

        String sql = """
                INSERT INTO "user" (
                    id,
                    username,
                    password,
                    first_name,
                    last_name,
                    role,
                    created
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                id,
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                createdTs
        );

        user.setId(id);
        user.setCreated(created);
        return user;
    }
}