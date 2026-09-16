package com.electricity.billing.dao;

import com.electricity.billing.model.User;
import com.electricity.billing.model.UsageType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int saveUser(User user) {

        String sql = """
                INSERT INTO users (name, mobile_number, usage_type)
                VALUES (?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                user.getName(),
                user.getMobileNumber(),
                user.getUsageType().name()
        );
    }
    
    public User findByServiceId(Long serviceId) {

        String sql = """
                SELECT service_id, name, mobile_number, usage_type
                FROM users
                WHERE service_id = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new User(
                        rs.getLong("service_id"),
                        rs.getString("name"),
                        rs.getString("mobile_number"),
                        UsageType.valueOf(rs.getString("usage_type"))
                ),
                serviceId
        );
    }
}