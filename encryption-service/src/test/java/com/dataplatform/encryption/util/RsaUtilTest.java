package com.dataplatform.encryption.util;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

class RsaUtilTest {

    @Test
    void testGenerateKeyPair() {
        KeyPair keyPair = RsaUtil.generateKeyPair();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublic());
        assertNotNull(keyPair.getPrivate());
    }

    @Test
    void testGetPublicKeyBase64() {
        KeyPair keyPair = RsaUtil.generateKeyPair();
        String publicKey = RsaUtil.getPublicKeyBase64(keyPair);
        assertNotNull(publicKey);
        assertFalse(publicKey.isEmpty());
    }

    @Test
    void testGetPrivateKeyBase64() {
        KeyPair keyPair = RsaUtil.generateKeyPair();
        String privateKey = RsaUtil.getPrivateKeyBase64(keyPair);
        assertNotNull(privateKey);
        assertFalse(privateKey.isEmpty());
    }

    @Test
    void testEncryptDecrypt() {
        String plaintext = "Hello, RSA!";
        KeyPair keyPair = RsaUtil.generateKeyPair();
        String publicKey = RsaUtil.getPublicKeyBase64(keyPair);
        String privateKey = RsaUtil.getPrivateKeyBase64(keyPair);

        String ciphertext = RsaUtil.encrypt(plaintext, publicKey);
        assertNotNull(ciphertext);

        String decrypted = RsaUtil.decrypt(ciphertext, privateKey);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testSignAndVerify() {
        String data = "Test data for signing";
        KeyPair keyPair = RsaUtil.generateKeyPair();
        String publicKey = RsaUtil.getPublicKeyBase64(keyPair);
        String privateKey = RsaUtil.getPrivateKeyBase64(keyPair);

        String signature = RsaUtil.sign(data, privateKey);
        assertNotNull(signature);

        boolean valid = RsaUtil.verify(data, signature, publicKey);
        assertTrue(valid);
    }

    @Test
    void testSignAndVerifyWithDifferentData() {
        String data = "Original data";
        String differentData = "Modified data";
        KeyPair keyPair = RsaUtil.generateKeyPair();
        String publicKey = RsaUtil.getPublicKeyBase64(keyPair);
        String privateKey = RsaUtil.getPrivateKeyBase64(keyPair);

        String signature = RsaUtil.sign(data, privateKey);
        boolean valid = RsaUtil.verify(differentData, signature, publicKey);
        assertFalse(valid);
    }
}