package com.project.ins.policy.model;

public enum PolicyStatus {

    ACTIVE("Active"),
    INACTIVE("Inactive"),
    CANCELLED("Canceled"),
    PENDING("Pending");

    private final String status;


    PolicyStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
