package com.dataplatform.encryption.service;

import com.dataplatform.encryption.model.AlgorithmType;
import com.dataplatform.encryption.model.KeyInfo;
import com.dataplatform.encryption.util.AesUtil;
import com.dataplatform.encryption.util.RsaUtil;
import com.dataplatform.encryption.util.Sm4Util;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
public class KeyManagementService {

    private final Map<String, KeyInfo> keyStore = new ConcurrentHashMap<>();
    private final Map<String, String> keyMaterialStore = new ConcurrentHashMap<>();

    public KeyInfo generateKey(AlgorithmType algorithm) {
        String keyId = UUID.randomUUID().toString();
        String keyMaterial;

        switch (algorithm) {
            case AES:
                keyMaterial = AesUtil.generateKey();
                break;
            case RSA:
                keyMaterial = RsaUtil.getPublicKeyBase64(RsaUtil.generateKeyPair());
                break;
            case SM4:
                keyMaterial = Sm4Util.generateKey();
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }

        KeyInfo keyInfo = KeyInfo.builder()
                .keyId(keyId)
                .algorithm(algorithm)
                .createdAt(LocalDateTime.now())
                .description("Auto-generated " + algorithm + " key")
                .build();

        keyStore.put(keyId, keyInfo);
        keyMaterialStore.put(keyId, keyMaterial);

        return keyInfo;
    }

    public void storeKey(String keyId, AlgorithmType algorithm, String keyMaterial) {
        KeyInfo keyInfo = KeyInfo.builder()
                .keyId(keyId)
                .algorithm(algorithm)
                .createdAt(LocalDateTime.now())
                .description("Stored key")
                .build();

        keyStore.put(keyId, keyInfo);
        keyMaterialStore.put(keyId, keyMaterial);
    }

    public String retrieveKey(String keyId) {
        return keyMaterialStore.get(keyId);
    }

    public List<KeyInfo> listKeys() {
        return new ArrayList<>(keyStore.values());
    }
}