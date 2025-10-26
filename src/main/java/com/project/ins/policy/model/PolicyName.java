package com.project.ins.policy.model;

public enum PolicyName {

    PERSON("Life insurance"),
    VEHICLE("Vehicle insurance"),
    PROPERTY("Property insurance");

    private final String name;

    private PolicyName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
