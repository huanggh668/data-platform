package com.dataplatform.encryption.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataplatform.encryption.dto.EncryptionAlgorithmRequest;
import com.dataplatform.encryption.dto.EncryptionAlgorithmResponse;
import com.dataplatform.encryption.entity.EncryptionAlgorithmEntity;
import com.dataplatform.encryption.mapper.EncryptionAlgorithmMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionAlgorithmService {

    private final EncryptionAlgorithmMapper encryptionAlgorithmMapper;

    public EncryptionAlgorithmResponse create(EncryptionAlgorithmRequest request) {
        EncryptionAlgorithmEntity entity = mapToEntity(request);
        encryptionAlgorithmMapper.insert(entity);
        log.info("Created encryption algorithm: {}", entity.getName());
        return mapToResponse(entity);
    }

    public EncryptionAlgorithmResponse update(Long id, EncryptionAlgorithmRequest request) {
        EncryptionAlgorithmEntity entity = encryptionAlgorithmMapper.selectById(id);
        if (entity == null) {
            throw new IllegalArgumentException("Algorithm not found: " + id);
        }
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setKeyLength(request.getKeyLength());
        entity.setMode(request.getMode());
        entity.setPadding(request.getPadding());
        entity.setConfig(request.getConfig());
        entity.setUpdatedAt(LocalDateTime.now());
        encryptionAlgorithmMapper.updateById(entity);
        log.info("Updated encryption algorithm: {}", id);
        return mapToResponse(entity);
    }

    public void delete(Long id) {
        encryptionAlgorithmMapper.deleteById(id);
        log.info("Deleted encryption algorithm: {}", id);
    }

    public EncryptionAlgorithmResponse getById(Long id) {
        EncryptionAlgorithmEntity entity = encryptionAlgorithmMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return mapToResponse(entity);
    }

    public IPage<EncryptionAlgorithmResponse> page(int page, int size) {
        Page<EncryptionAlgorithmEntity> pageParam = new Page<>(page, size);
        Page<EncryptionAlgorithmEntity> result = encryptionAlgorithmMapper.selectPage(pageParam, null);
        return result.convert(this::mapToResponse);
    }

    private EncryptionAlgorithmEntity mapToEntity(EncryptionAlgorithmRequest request) {
        EncryptionAlgorithmEntity entity = new EncryptionAlgorithmEntity();
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setKeyLength(request.getKeyLength());
        entity.setMode(request.getMode());
        entity.setPadding(request.getPadding());
        entity.setConfig(request.getConfig());
        return entity;
    }

    private EncryptionAlgorithmResponse mapToResponse(EncryptionAlgorithmEntity entity) {
        return EncryptionAlgorithmResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .keyLength(entity.getKeyLength())
                .mode(entity.getMode())
                .padding(entity.getPadding())
                .config(entity.getConfig())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}