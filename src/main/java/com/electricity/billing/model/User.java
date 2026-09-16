package com.electricity.billing.model;

public class User {

    private Long serviceId;
    private String name;
    private String mobileNumber;
    private UsageType usageType;

    public User() {
    }

    public User(Long serviceId, String name, String mobileNumber, UsageType usageType) {
        this.serviceId = serviceId;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.usageType = usageType;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public UsageType getUsageType() {
        return usageType;
    }

    public void setUsageType(UsageType usageType) {
        this.usageType = usageType;
    }
}