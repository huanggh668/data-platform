package com.dataplatform.encryption.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataplatform.encryption.dto.EncryptionJobRequest;
import com.dataplatform.encryption.dto.EncryptionJobResponse;
import com.dataplatform.encryption.entity.EncryptionJobEntity;
import com.dataplatform.encryption.mapper.EncryptionJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionJobService {

    private final EncryptionJobMapper encryptionJobMapper;

    public EncryptionJobResponse create(EncryptionJobRequest request) {
        EncryptionJobEntity entity = mapToEntity(request);
        entity.setStatus("PENDING");
        encryptionJobMapper.insert(entity);
        log.info("Created encryption job: {}", entity.getName());
        return mapToResponse(entity);
    }

    public EncryptionJobResponse update(Long id, EncryptionJobRequest request) {
        EncryptionJobEntity entity = encryptionJobMapper.selectById(id);
        if (entity == null) {
            throw new IllegalArgumentException("Job not found: " + id);
        }
        entity.setName(request.getName());
        entity.setAlgorithmId(request.getAlgorithmId());
        entity.setSourcePath(request.getSourcePath());
        entity.setTargetPath(request.getTargetPath());
        entity.setOperation(request.getOperation());
        entity.setCronExpression(request.getCronExpression());
        entity.setIsScheduled(request.getIsScheduled());
        entity.setUpdatedAt(LocalDateTime.now());
        encryptionJobMapper.updateById(entity);
        log.info("Updated encryption job: {}", id);
        return mapToResponse(entity);
    }

    public void delete(Long id) {
        encryptionJobMapper.deleteById(id);
        log.info("Deleted encryption job: {}", id);
    }

    public EncryptionJobResponse getById(Long id) {
        EncryptionJobEntity entity = encryptionJobMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return mapToResponse(entity);
    }

    public IPage<EncryptionJobResponse> page(int page, int size) {
        Page<EncryptionJobEntity> pageParam = new Page<>(page, size);
        Page<EncryptionJobEntity> result = encryptionJobMapper.selectPage(pageParam, null);
        return result.convert(this::mapToResponse);
    }

    @Async
    public void executeAsync(Long id) {
        execute(id);
    }

    public EncryptionJobResponse execute(Long id) {
        EncryptionJobEntity entity = encryptionJobMapper.selectById(id);
        if (entity == null) {
            throw new IllegalArgumentException("Job not found: " + id);
        }

        entity.setStatus("RUNNING");
        entity.setExecuteAt(LocalDateTime.now());
        encryptionJobMapper.updateById(entity);
        log.info("Executing encryption job: {}", id);

        try {
            // Placeholder for actual encryption logic
            // In a real implementation, this would:
            // 1. Load the algorithm configuration
            // 2. Read the source file
            // 3. Perform encryption/decryption
            // 4. Write to target path
            String result = "Execution completed successfully";
            
            entity.setStatus("COMPLETED");
            entity.setCompletedAt(LocalDateTime.now());
            entity.setResult(result);
            log.info("Encryption job completed: {}", id);
        } catch (Exception e) {
            entity.setStatus("FAILED");
            entity.setCompletedAt(LocalDateTime.now());
            entity.setResult("Error: " + e.getMessage());
            log.error("Encryption job failed: {}", id, e);
        }

        entity.setUpdatedAt(LocalDateTime.now());
        encryptionJobMapper.updateById(entity);
        return mapToResponse(entity);
    }

    private EncryptionJobEntity mapToEntity(EncryptionJobRequest request) {
        EncryptionJobEntity entity = new EncryptionJobEntity();
        entity.setName(request.getName());
        entity.setAlgorithmId(request.getAlgorithmId());
        entity.setSourcePath(request.getSourcePath());
        entity.setTargetPath(request.getTargetPath());
        entity.setOperation(request.getOperation());
        entity.setCronExpression(request.getCronExpression());
        entity.setIsScheduled(request.getIsScheduled() != null ? request.getIsScheduled() : false);
        return entity;
    }

    private EncryptionJobResponse mapToResponse(EncryptionJobEntity entity) {
        return EncryptionJobResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .algorithmId(entity.getAlgorithmId())
                .sourcePath(entity.getSourcePath())
                .targetPath(entity.getTargetPath())
                .operation(entity.getOperation())
                .status(entity.getStatus())
                .cronExpression(entity.getCronExpression())
                .isScheduled(entity.getIsScheduled())
                .executeAt(entity.getExecuteAt())
                .completedAt(entity.getCompletedAt())
                .result(entity.getResult())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}