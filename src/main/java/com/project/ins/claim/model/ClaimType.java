package com.project.ins.claim.model;

public enum ClaimType {

    LIFE("Life"),
    HOME("Home"),
    VEHICLE("Vehicle");

    private final String claimType;
    private ClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getClaimType() {
        return claimType;
    }
}
