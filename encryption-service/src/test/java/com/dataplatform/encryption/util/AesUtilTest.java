package com.dataplatform.encryption.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AesUtilTest {

    @Test
    void testGenerateKey() {
        String key = AesUtil.generateKey();
        assertNotNull(key);
        assertFalse(key.isEmpty());
    }

    @Test
    void testEncryptDecrypt() {
        String plaintext = "Hello, World!";
        String key = AesUtil.generateKey();

        String ciphertext = AesUtil.encrypt(plaintext, key);
        assertNotNull(ciphertext);
        assertFalse(ciphertext.isEmpty());

        String decrypted = AesUtil.decrypt(ciphertext, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptDecryptWithSpecialCharacters() {
        String plaintext = "你好世界!@#$%^&*()";
        String key = AesUtil.generateKey();

        String ciphertext = AesUtil.encrypt(plaintext, key);
        String decrypted = AesUtil.decrypt(ciphertext, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptDecryptLongText() {
        String plaintext = "A".repeat(1000);
        String key = AesUtil.generateKey();

        String ciphertext = AesUtil.encrypt(plaintext, key);
        String decrypted = AesUtil.decrypt(ciphertext, key);
        assertEquals(plaintext, decrypted);
    }
}