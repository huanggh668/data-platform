package com.dataplatform.encryption.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Sm4UtilTest {

    @Test
    void testGenerateKey() {
        String key = Sm4Util.generateKey();
        assertNotNull(key);
        assertFalse(key.isEmpty());
    }

    @Test
    void testEncryptDecrypt() {
        String plaintext = "Hello, SM4!";
        String key = Sm4Util.generateKey();

        String ciphertext = Sm4Util.encrypt(plaintext, key);
        assertNotNull(ciphertext);
        assertFalse(ciphertext.isEmpty());

        String decrypted = Sm4Util.decrypt(ciphertext, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptDecryptWithSpecialCharacters() {
        String plaintext = "你好SM4!@#$%^&*()";
        String key = Sm4Util.generateKey();

        String ciphertext = Sm4Util.encrypt(plaintext, key);
        String decrypted = Sm4Util.decrypt(ciphertext, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptDecryptLongText() {
        String plaintext = "A".repeat(1000);
        String key = Sm4Util.generateKey();

        String ciphertext = Sm4Util.encrypt(plaintext, key);
        String decrypted = Sm4Util.decrypt(ciphertext, key);
        assertEquals(plaintext, decrypted);
    }
}