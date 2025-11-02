package com.project.ins.claim.model;

public enum ClaimStatus {

    REGISTERED("Registered"),
    REVIEWING("Reviewing"),
    PAID("Paid"),
    DECLINED("Declined"),
    APPROVED("Approved");

    private final String status;

    private ClaimStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
