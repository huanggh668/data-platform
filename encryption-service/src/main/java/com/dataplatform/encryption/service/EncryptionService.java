package com.dataplatform.encryption.service;

import com.dataplatform.encryption.model.AlgorithmType;
import com.dataplatform.encryption.util.AesUtil;
import com.dataplatform.encryption.util.RsaUtil;
import com.dataplatform.encryption.util.Sm4Util;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    public String encrypt(String plaintext, AlgorithmType algorithm, String key) {
        switch (algorithm) {
            case AES:
                return AesUtil.encrypt(plaintext, key);
            case RSA:
                return RsaUtil.encrypt(plaintext, key);
            case SM4:
                return Sm4Util.encrypt(plaintext, key);
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    public String decrypt(String ciphertext, AlgorithmType algorithm, String key) {
        switch (algorithm) {
            case AES:
                return AesUtil.decrypt(ciphertext, key);
            case RSA:
                return RsaUtil.decrypt(ciphertext, key);
            case SM4:
                return Sm4Util.decrypt(ciphertext, key);
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    public String sign(String data, String privateKey) {
        return RsaUtil.sign(data, privateKey);
    }

    public boolean verify(String data, String signature, String publicKey) {
        return RsaUtil.verify(data, signature, publicKey);
    }
}