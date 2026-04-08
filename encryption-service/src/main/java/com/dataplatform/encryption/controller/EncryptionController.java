package com.dataplatform.encryption.controller;

import com.dataplatform.encryption.dto.*;
import com.dataplatform.encryption.model.AlgorithmType;
import com.dataplatform.encryption.model.KeyInfo;
import com.dataplatform.encryption.service.EncryptionService;
import com.dataplatform.encryption.service.KeyManagementService;
import com.dataplatform.encryption.util.RsaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EncryptionController {

    private final EncryptionService encryptionService;
    private final KeyManagementService keyManagementService;

    @PostMapping("/encrypt")
    public ResponseEntity<EncryptResponse> encrypt(@RequestBody EncryptRequest request) {
        String ciphertext = encryptionService.encrypt(
                request.getPlaintext(),
                request.getAlgorithm(),
                request.getKey()
        );
        return ResponseEntity.ok(EncryptResponse.builder()
                .ciphertext(ciphertext)
                .algorithm(request.getAlgorithm())
                .build());
    }

    @PostMapping("/decrypt")
    public ResponseEntity<DecryptResponse> decrypt(@RequestBody DecryptRequest request) {
        String plaintext = encryptionService.decrypt(
                request.getCiphertext(),
                request.getAlgorithm(),
                request.getKey()
        );
        return ResponseEntity.ok(DecryptResponse.builder()
                .plaintext(plaintext)
                .build());
    }

    @PostMapping("/sign")
    public ResponseEntity<SignResponse> sign(@RequestBody SignRequest request) {
        String signature = encryptionService.sign(request.getData(), request.getPrivateKey());
        return ResponseEntity.ok(SignResponse.builder()
                .signature(signature)
                .build());
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@RequestBody VerifyRequest request) {
        boolean valid = encryptionService.verify(
                request.getData(),
                request.getSignature(),
                request.getPublicKey()
        );
        return ResponseEntity.ok(VerifyResponse.builder()
                .valid(valid)
                .build());
    }

    @PostMapping("/keys/generate")
    public ResponseEntity<KeyGenerateResponse> generateKey(@RequestBody KeyGenerateRequest request) {
        KeyInfo keyInfo = keyManagementService.generateKey(request.getAlgorithm());
        String privateKey = null;
        String publicKey = null;

        if (request.getAlgorithm() == AlgorithmType.RSA) {
            var keyPair = RsaUtil.generateKeyPair();
            privateKey = RsaUtil.getPrivateKeyBase64(keyPair);
            publicKey = RsaUtil.getPublicKeyBase64(keyPair);
            keyManagementService.storeKey(keyInfo.getKeyId(), AlgorithmType.RSA, privateKey);
        } else {
            privateKey = keyManagementService.retrieveKey(keyInfo.getKeyId());
            publicKey = privateKey;
        }

        return ResponseEntity.ok(KeyGenerateResponse.builder()
                .keyId(keyInfo.getKeyId())
                .publicKey(publicKey)
                .privateKey(privateKey)
                .build());
    }

    @PostMapping("/keys/store")
    public ResponseEntity<Void> storeKey(@RequestBody KeyStoreRequest request) {
        keyManagementService.storeKey(
                request.getKeyId(),
                request.getAlgorithm(),
                request.getKeyMaterial()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/keys")
    public ResponseEntity<KeyListResponse> listKeys() {
        List<KeyInfo> keys = keyManagementService.listKeys();
        return ResponseEntity.ok(KeyListResponse.builder()
                .keys(keys)
                .build());
    }

    @GetMapping("/actuator/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}