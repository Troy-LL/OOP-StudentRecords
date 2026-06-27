package com.srms.model;

public enum Role {
    ADMIN,
    STAFF;

    public static Role fromString(String value) {
        return Role.valueOf(value.trim().toUpperCase());
    }
}
