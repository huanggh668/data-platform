package com.dataplatform.encryption.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataplatform.encryption.dto.*;
import com.dataplatform.encryption.entity.EncryptionLog;
import com.dataplatform.encryption.model.AlgorithmType;
import com.dataplatform.encryption.model.KeyInfo;
import com.dataplatform.encryption.service.EncryptionService;
import com.dataplatform.encryption.service.KeyManagementService;
import com.dataplatform.encryption.util.RsaUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.KeyPair;
import java.util.List;

@Tag(name = "加密核心操作", description = "数据加密/解密、数字签名/验签、密钥生成与管理、操作日志查询")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EncryptionController {

    private final EncryptionService encryptionService;
    private final KeyManagementService keyManagementService;

    @Operation(summary = "加密数据", description = "支持 AES/RSA/SM4 等算法，key 传算法密钥或密钥ID")
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

    @Operation(summary = "解密数据", description = "传入密文、算法及密钥，返回明文")
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

    @Operation(summary = "数字签名", description = "使用 RSA 私钥对数据签名")
    @PostMapping("/sign")
    public ResponseEntity<SignResponse> sign(@RequestBody SignRequest request) {
        String signature = encryptionService.sign(request.getData(), request.getPrivateKey());
        return ResponseEntity.ok(SignResponse.builder()
                .signature(signature)
                .build());
    }

    @Operation(summary = "验证签名", description = "使用 RSA 公钥验证签名有效性")
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

    @Operation(summary = "生成密钥", description = "按算法类型生成对称或非对称密钥，RSA 返回公私钥对")
    @PostMapping("/keys/generate")
    public ResponseEntity<KeyGenerateResponse> generateKey(@RequestBody KeyGenerateRequest request) {
        KeyInfo keyInfo = keyManagementService.generateKey(request.getAlgorithm());
        String privateKey = null;
        String publicKey = null;

        if (request.getAlgorithm() == AlgorithmType.RSA) {
            KeyPair keyPair = RsaUtil.generateKeyPair();
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

    @Operation(summary = "存储自定义密钥")
    @PostMapping("/keys/store")
    public ResponseEntity<Void> storeKey(@RequestBody KeyStoreRequest request) {
        keyManagementService.storeKey(
                request.getKeyId(),
                request.getAlgorithm(),
                request.getKeyMaterial()
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "列出所有密钥")
    @GetMapping("/keys")
    public ResponseEntity<KeyListResponse> listKeys() {
        List<KeyInfo> keys = keyManagementService.listKeys();
        return ResponseEntity.ok(KeyListResponse.builder()
                .keys(keys)
                .build());
    }

    @Operation(summary = "分页查询加密日志")
    @GetMapping("/logs")
    public ResponseEntity<Page<EncryptionLog>> queryLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        Page<EncryptionLog> logs = encryptionService.queryLogs(page, size);
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "根据ID查询加密日志")
    @GetMapping("/logs/{id}")
    public ResponseEntity<EncryptionLog> getLogById(
            @Parameter(description = "日志ID") @PathVariable Long id) {
        EncryptionLog log = encryptionService.getLogById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }

    @Operation(summary = "健康检查")
    @GetMapping("/actuator/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
