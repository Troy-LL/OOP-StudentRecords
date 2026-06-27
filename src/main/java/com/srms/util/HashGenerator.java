package com.srms.util;

/** Utility to print seed password hashes for db/schema.sql. */
public final class HashGenerator {
    public static void main(String[] args) {
        System.out.println(PasswordUtil.hashPasswordWithSalt("admin123", PasswordUtil.SEED_SALT));
    }
}
