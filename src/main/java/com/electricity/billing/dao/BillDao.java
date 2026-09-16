package com.electricity.billing.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.electricity.billing.model.Bill;

@Repository
public class BillDao {

    private final JdbcTemplate jdbcTemplate;

    public BillDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int saveBill(Bill bill) {

        String sql = """
                INSERT INTO bills
                (service_id, units_used, amount, billing_period)
                VALUES (?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                bill.getServiceId(),
                bill.getUnitsUsed(),
                bill.getAmount(),
                bill.getBillingPeriod()
        );
    }

    public List<Bill> findByServiceId(Long serviceId) {

        String sql = """
                SELECT bill_id, service_id, units_used, amount, billing_period
                FROM bills
                WHERE service_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Bill(
                        rs.getLong("bill_id"),
                        rs.getLong("service_id"),
                        rs.getInt("units_used"),
                        rs.getDouble("amount"),
                        rs.getString("billing_period")
                ),
                serviceId
        );
    }
}