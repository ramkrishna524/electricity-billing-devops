package com.electricity.billing.model;

public class Bill {

    private Long billId;
    private Long serviceId;
    private int unitsUsed;
    private Double amount;
    private String billingPeriod;

    public Bill() {
    }

    public Bill(Long billId, Long serviceId, int unitsUsed,
                Double amount, String billingPeriod) {
        this.billId = billId;
        this.serviceId = serviceId;
        this.unitsUsed = unitsUsed;
        this.amount = amount;
        this.billingPeriod = billingPeriod;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public int getUnitsUsed() {
        return unitsUsed;
    }

    public void setUnitsUsed(int unitsUsed) {
        this.unitsUsed = unitsUsed;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }
}
