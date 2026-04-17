package com.dataplatform.watermark.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataplatform.watermark.dto.WatermarkJobRequest;
import com.dataplatform.watermark.dto.WatermarkJobResponse;
import com.dataplatform.watermark.entity.WatermarkJobEntity;
import com.dataplatform.watermark.mapper.WatermarkJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatermarkJobService {
    private final WatermarkJobMapper watermarkJobMapper;

    public WatermarkJobResponse create(WatermarkJobRequest request) {
        WatermarkJobEntity entity = new WatermarkJobEntity();
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setFactorIds(request.getFactorIds());
        entity.setSourcePath(request.getSourcePath());
        entity.setTargetPath(request.getTargetPath());
        entity.setStatus("PENDING");
        entity.setCronExpression(request.getCronExpression());
        entity.setIsScheduled(request.getIsScheduled() != null ? request.getIsScheduled() : false);
        entity.setExecuteAt(request.getExecuteAt());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkJobMapper.insert(entity);
        log.info("Created watermark job: id={}, name={}", entity.getId(), entity.getName());
        return toResponse(entity);
    }

    public WatermarkJobResponse update(Long id, WatermarkJobRequest request) {
        WatermarkJobEntity entity = watermarkJobMapper.selectById(id);
        if (entity == null) {
            log.warn("Watermark job not found: id={}", id);
            return null;
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getType() != null) {
            entity.setType(request.getType());
        }
        if (request.getFactorIds() != null) {
            entity.setFactorIds(request.getFactorIds());
        }
        if (request.getSourcePath() != null) {
            entity.setSourcePath(request.getSourcePath());
        }
        if (request.getTargetPath() != null) {
            entity.setTargetPath(request.getTargetPath());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getCronExpression() != null) {
            entity.setCronExpression(request.getCronExpression());
        }
        if (request.getIsScheduled() != null) {
            entity.setIsScheduled(request.getIsScheduled());
        }
        if (request.getExecuteAt() != null) {
            entity.setExecuteAt(request.getExecuteAt());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkJobMapper.updateById(entity);
        log.info("Updated watermark job: id={}", id);
        return toResponse(entity);
    }

    public void delete(Long id) {
        watermarkJobMapper.deleteById(id);
        log.info("Deleted watermark job: id={}", id);
    }

    public WatermarkJobResponse getById(Long id) {
        WatermarkJobEntity entity = watermarkJobMapper.selectById(id);
        return entity != null ? toResponse(entity) : null;
    }

    public List<WatermarkJobResponse> list() {
        LambdaQueryWrapper<WatermarkJobEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WatermarkJobEntity::getCreatedAt);
        return watermarkJobMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Async
    public WatermarkJobResponse execute(Long id) {
        WatermarkJobEntity entity = watermarkJobMapper.selectById(id);
        if (entity == null) {
            log.warn("Watermark job not found for execution: id={}", id);
            return null;
        }
        
        entity.setStatus("RUNNING");
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkJobMapper.updateById(entity);
        log.info("Started executing watermark job: id={}", id);
        
        try {
            // Simulate job execution
            // In real implementation, this would call watermark embedding/extraction logic
            entity.setStatus("COMPLETED");
            entity.setCompletedAt(LocalDateTime.now());
            entity.setResult("Job executed successfully");
        } catch (Exception e) {
            log.error("Job execution failed: id={}", id, e);
            entity.setStatus("FAILED");
            entity.setCompletedAt(LocalDateTime.now());
            entity.setResult("Execution failed: " + e.getMessage());
        }
        
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkJobMapper.updateById(entity);
        log.info("Completed watermark job execution: id={}, status={}", id, entity.getStatus());
        
        return toResponse(entity);
    }

    private WatermarkJobResponse toResponse(WatermarkJobEntity entity) {
        return WatermarkJobResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .factorIds(entity.getFactorIds())
                .sourcePath(entity.getSourcePath())
                .targetPath(entity.getTargetPath())
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