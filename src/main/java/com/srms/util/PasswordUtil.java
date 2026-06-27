package com.srms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    private static final int SALT_BYTES = 16;

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + hashWithSalt(password, salt);
    }

    public static boolean verifyPassword(String password, String storedValue) {
        if (password == null || storedValue == null || !storedValue.contains(":")) {
            return false;
        }
        int separator = storedValue.indexOf(':');
        byte[] salt = storedValue.substring(0, separator).getBytes(StandardCharsets.UTF_8);
        String expectedHash = storedValue.substring(separator + 1);
        return expectedHash.equals(hashWithSalt(password, salt));
    }

    private static String hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return Base64.getEncoder().encodeToString(
                    digest.digest(password.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
