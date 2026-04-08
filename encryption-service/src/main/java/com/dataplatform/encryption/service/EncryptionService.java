package com.dataplatform.encryption.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataplatform.encryption.entity.EncryptionLog;
import com.dataplatform.encryption.mapper.EncryptionLogMapper;
import com.dataplatform.encryption.model.AlgorithmType;
import com.dataplatform.encryption.util.AesUtil;
import com.dataplatform.encryption.util.RsaUtil;
import com.dataplatform.encryption.util.Sm4Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    private final EncryptionLogMapper encryptionLogMapper;

    public String encrypt(String plaintext, AlgorithmType algorithm, String key) {
        String ciphertext;
        switch (algorithm) {
            case AES:
                ciphertext = AesUtil.encrypt(plaintext, key);
                break;
            case RSA:
                ciphertext = RsaUtil.encrypt(plaintext, key);
                break;
            case SM4:
                ciphertext = Sm4Util.encrypt(plaintext, key);
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
        saveLog(algorithm.name(), "ENCRYPT", plaintext, ciphertext, null, "SUCCESS");
        return ciphertext;
    }

    public String decrypt(String ciphertext, AlgorithmType algorithm, String key) {
        String plaintext;
        switch (algorithm) {
            case AES:
                plaintext = AesUtil.decrypt(ciphertext, key);
                break;
            case RSA:
                plaintext = RsaUtil.decrypt(ciphertext, key);
                break;
            case SM4:
                plaintext = Sm4Util.decrypt(ciphertext, key);
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
        saveLog(algorithm.name(), "DECRYPT", plaintext, ciphertext, null, "SUCCESS");
        return plaintext;
    }

    public String sign(String data, String privateKey) {
        return RsaUtil.sign(data, privateKey);
    }

    public boolean verify(String data, String signature, String publicKey) {
        return RsaUtil.verify(data, signature, publicKey);
    }

    private void saveLog(String algorithm, String operation, String originalData, String encryptedData, String keyId, String status) {
        EncryptionLog log = new EncryptionLog();
        log.setAlgorithm(algorithm);
        log.setOperation(operation);
        log.setOriginalData(originalData);
        log.setEncryptedData(encryptedData);
        log.setKeyId(keyId);
        log.setStatus(status);
        encryptionLogMapper.insert(log);
    }

    public Page<EncryptionLog> queryLogs(int page, int size) {
        Page<EncryptionLog> pageParam = new Page<>(page, size);
        return encryptionLogMapper.selectPage(pageParam, null);
    }

    public EncryptionLog getLogById(Long id) {
        return encryptionLogMapper.selectById(id);
    }

    public List<EncryptionLog> queryLogsByKeyId(String keyId) {
        QueryWrapper<EncryptionLog> wrapper = new QueryWrapper<>();
        wrapper.eq("keyId", keyId);
        return encryptionLogMapper.selectList(wrapper);
    }
}