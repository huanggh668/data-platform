package com.dataplatform.encryption.util;

import java.security.SecureRandom;
import java.util.Base64;

public class Sm4Util {

    private static final String ALGORITHM = "SM4";
    private static final int KEY_SIZE = 128;
    private static final int IV_SIZE = 16;

    public static String generateKey() {
        try {
            byte[] keyBytes = new byte[KEY_SIZE / 8];
            new SecureRandom().nextBytes(keyBytes);
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SM4 key", e);
        }
    }

    public static String encrypt(String plaintext, String keyBase64) {
        throw new RuntimeException("SM4 not implemented");
    }

    public static String decrypt(String ciphertextBase64, String keyBase64) {
        throw new RuntimeException("SM4 not implemented");
    }
}