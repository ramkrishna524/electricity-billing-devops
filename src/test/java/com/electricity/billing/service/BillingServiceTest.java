package com.electricity.billing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.electricity.billing.model.UsageType;

public class BillingServiceTest {
	
    @Test
    void shouldCalculateHouseholdBill() {

        BillingService billingService = new BillingService();

        double result = billingService.calculateBill(
                UsageType.HOUSEHOLD,
                250
        );

        assertEquals(400.0, result);
    }
    
    @Test
    void shouldCalculateIndustryBill() {

        BillingService billingService = new BillingService();

        double result = billingService.calculateBill(
                UsageType.INDUSTRY,
                600
        );

        assertEquals(1550.0, result);
    }
    
    @Test
    void shouldCalculateHouseholdBillAt200Units() {

        BillingService billingService = new BillingService();

        double result = billingService.calculateBill(
                UsageType.HOUSEHOLD,
                200
        );

        assertEquals(300.0, result);
    }

    @Test
    void shouldCalculateIndustryBillAt500Units() {

        BillingService billingService = new BillingService();

        double result = billingService.calculateBill(
                UsageType.INDUSTRY,
                500
        );

        assertEquals(1250.0, result);
    }
    
    @Test
    void shouldRejectNegativeUnits() {

        BillingService billingService = new BillingService();

        assertThrows(
                IllegalArgumentException.class,
                () -> billingService.calculateBill(
                        UsageType.HOUSEHOLD,
                        -10
                )
        );
    }
}