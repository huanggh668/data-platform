package com.dataplatform.encryption.util;

import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.cbc.PaddedBlockingCBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;

import java.security.SecureRandom;

public class Sm4Util {

    private static final String ALGORITHM = "SM4";
    private static final int KEY_SIZE = 128;
    private static final int IV_SIZE = 16;

    public static String generateKey() {
        try {
            byte[] keyBytes = new byte[KEY_SIZE / 8];
            new SecureRandom().nextBytes(keyBytes);
            return Base64.toBase64String(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SM4 key", e);
        }
    }

    public static String encrypt(String plaintext, String keyBase64) {
        try {
            byte[] keyBytes = Base64.decode(keyBase64);
            byte[] iv = new byte[IV_SIZE];
            new SecureRandom().nextBytes(iv);

            PaddedBlockingCBCBlockCipher cipher = new PaddedBlockingCBCBlockCipher(new SM4Engine());
            ParametersWithIV params = new ParametersWithIV(new KeyParameter(keyBytes), iv);
            cipher.init(true, params);

            byte[] input = plaintext.getBytes("UTF-8");
            byte[] encrypted = new byte[cipher.getOutputSize(input.length)];
            int len = cipher.processBytes(input, 0, input.length, encrypted, 0);
            len += cipher.doFinal(encrypted, len);

            byte[] combined = new byte[IV_SIZE + len];
            System.arraycopy(iv, 0, combined, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, combined, IV_SIZE, len);

            return Base64.toBase64String(combined);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt with SM4", e);
        }
    }

    public static String decrypt(String ciphertextBase64, String keyBase64) {
        try {
            byte[] combined = Base64.decode(ciphertextBase64);
            byte[] keyBytes = Base64.decode(keyBase64);

            byte[] iv = new byte[IV_SIZE];
            byte[] encrypted = new byte[combined.length - IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE);
            System.arraycopy(combined, IV_SIZE, encrypted, 0, encrypted.length);

            PaddedBlockingCBCBlockCipher cipher = new PaddedBlockingCBCBlockCipher(new SM4Engine());
            ParametersWithIV params = new ParametersWithIV(new KeyParameter(keyBytes), iv);
            cipher.init(false, params);

            byte[] decrypted = new byte[cipher.getOutputSize(encrypted.length)];
            int len = cipher.processBytes(encrypted, 0, encrypted.length, decrypted, 0);
            len += cipher.doFinal(decrypted, len);

            byte[] result = new byte[len];
            System.arraycopy(decrypted, 0, result, 0, len);
            return new String(result, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt with SM4", e);
        }
    }
}