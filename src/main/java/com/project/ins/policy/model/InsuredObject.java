package com.project.ins.policy.model;

public enum InsuredObject {

    PERSON("Life insurance"),
    VEHICLE("Vehicle insurance"),
    PROPERTY("Property insurance");

    private String description;

    private InsuredObject(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
