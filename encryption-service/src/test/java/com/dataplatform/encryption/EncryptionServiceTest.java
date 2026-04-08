package com.dataplatform.encryption;

import com.dataplatform.encryption.model.AlgorithmType;
import com.dataplatform.encryption.service.EncryptionService;
import com.dataplatform.encryption.util.AesUtil;
import com.dataplatform.encryption.util.RsaUtil;
import com.dataplatform.encryption.util.Sm4Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
    }

    @Test
    void testEncryptDecryptWithAES() {
        String plaintext = "Hello, AES!";
        String key = AesUtil.generateKey();

        String ciphertext = encryptionService.encrypt(plaintext, AlgorithmType.AES, key);
        assertNotNull(ciphertext);

        String decrypted = encryptionService.decrypt(ciphertext, AlgorithmType.AES, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptDecryptWithRSA() {
        String plaintext = "Hello, RSA!";
        var keyPair = RsaUtil.generateKeyPair();
        String publicKey = RsaUtil.getPublicKeyBase64(keyPair);
        String privateKey = RsaUtil.getPrivateKeyBase64(keyPair);

        String ciphertext = encryptionService.encrypt(plaintext, AlgorithmType.RSA, publicKey);
        assertNotNull(ciphertext);

        String decrypted = encryptionService.decrypt(ciphertext, AlgorithmType.RSA, privateKey);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptDecryptWithSM4() {
        String plaintext = "Hello, SM4!";
        String key = Sm4Util.generateKey();

        String ciphertext = encryptionService.encrypt(plaintext, AlgorithmType.SM4, key);
        assertNotNull(ciphertext);

        String decrypted = encryptionService.decrypt(ciphertext, AlgorithmType.SM4, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testSignAndVerify() {
        String data = "Test data for signing";
        var keyPair = RsaUtil.generateKeyPair();
        String publicKey = RsaUtil.getPublicKeyBase64(keyPair);
        String privateKey = RsaUtil.getPrivateKeyBase64(keyPair);

        String signature = encryptionService.sign(data, privateKey);
        assertNotNull(signature);

        boolean valid = encryptionService.verify(data, signature, publicKey);
        assertTrue(valid);
    }

    @Test
    void testUnsupportedAlgorithm() {
        assertThrows(IllegalArgumentException.class, () -> {
            encryptionService.encrypt("test", AlgorithmType.AES, "invalid-key");
        });
    }
}