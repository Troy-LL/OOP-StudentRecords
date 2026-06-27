package com.srms.model;

/**
 * System user roles with role-based access control.
 */
public enum Role {
    ADMIN,
    STAFF;

    public static Role fromString(String value) {
        return Role.valueOf(value.trim().toUpperCase());
    }
}
