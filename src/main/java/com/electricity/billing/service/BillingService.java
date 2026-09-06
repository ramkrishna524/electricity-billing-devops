package com.electricity.billing.service;

import org.springframework.stereotype.Service;

import com.electricity.billing.model.UsageType;

@Service
public class BillingService {

    public double calculateBill(UsageType usageType, int units) {

        if (units < 0) {
            throw new IllegalArgumentException("Units cannot be negative");
        }

        if (usageType == UsageType.HOUSEHOLD) {

            if (units <= 200) {
                return units * 1.5;
            }

            return (200 * 1.5) + ((units - 200) * 2.0);

        } else if (usageType == UsageType.INDUSTRY) {

            if (units <= 500) {
                return units * 2.5;
            }

            return (500 * 2.5) + ((units - 500) * 3.0);
        }

        throw new IllegalArgumentException("Invalid usage type");
    }
}