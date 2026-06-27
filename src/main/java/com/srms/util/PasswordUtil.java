package com.srms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashing and verification using SHA-256 with salt.
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_BYTES = 16;
    public static final String SEED_SALT = "srms2024salt!!";

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return encode(salt) + ":" + hashWithSalt(password, salt);
    }

    public static String hashPasswordWithSalt(String password, String saltText) {
        byte[] salt = saltText.getBytes(StandardCharsets.UTF_8);
        return saltText + ":" + hashWithSalt(password, salt);
    }

    public static boolean verifyPassword(String password, String storedValue) {
        if (password == null || storedValue == null || !storedValue.contains(":")) {
            return false;
        }
        int separator = storedValue.indexOf(':');
        String saltText = storedValue.substring(0, separator);
        String expectedHash = storedValue.substring(separator + 1);
        byte[] salt = saltText.getBytes(StandardCharsets.UTF_8);
        return expectedHash.equals(hashWithSalt(password, salt));
    }

    private static String hashWithSalt(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(salt);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
