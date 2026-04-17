package com.dataplatform.desensitization.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dataplatform.desensitization.dto.DesensitizationJobRequest;
import com.dataplatform.desensitization.dto.DesensitizationJobResponse;
import com.dataplatform.desensitization.entity.DesensitizationJobEntity;
import com.dataplatform.desensitization.mapper.DesensitizationJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesensitizationJobService {

    private final DesensitizationJobMapper desensitizationJobMapper;

    public DesensitizationJobResponse create(DesensitizationJobRequest request) {
        DesensitizationJobEntity entity = new DesensitizationJobEntity();
        entity.setName(request.getName());
        entity.setSourceId(request.getSourceId());
        entity.setTargetId(request.getTargetId());
        entity.setRuleIds(request.getRuleIds());
        entity.setCronExpression(request.getCronExpression());
        entity.setIsScheduled(request.getIsScheduled() != null ? request.getIsScheduled() : false);
        entity.setStatus("PENDING");
        desensitizationJobMapper.insert(entity);
        log.info("Created desensitization job: {}", entity.getId());
        return toResponse(entity);
    }

    public DesensitizationJobResponse update(Long id, DesensitizationJobRequest request) {
        DesensitizationJobEntity entity = desensitizationJobMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("DesensitizationJob not found: " + id);
        }
        entity.setName(request.getName());
        entity.setSourceId(request.getSourceId());
        entity.setTargetId(request.getTargetId());
        entity.setRuleIds(request.getRuleIds());
        entity.setCronExpression(request.getCronExpression());
        entity.setIsScheduled(request.getIsScheduled());
        desensitizationJobMapper.updateById(entity);
        log.info("Updated desensitization job: {}", id);
        return toResponse(entity);
    }

    public void delete(Long id) {
        desensitizationJobMapper.deleteById(id);
        log.info("Deleted desensitization job: {}", id);
    }

    public DesensitizationJobResponse getById(Long id) {
        DesensitizationJobEntity entity = desensitizationJobMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return toResponse(entity);
    }

    public List<DesensitizationJobResponse> listAll() {
        LambdaQueryWrapper<DesensitizationJobEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DesensitizationJobEntity::getCreatedAt);
        return desensitizationJobMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Async
    public void executeAsync(Long id) {
        log.info("Executing job asynchronously: {}", id);
        executeJob(id);
    }

    public DesensitizationJobResponse execute(Long id) {
        log.info("Executing job synchronously: {}", id);
        return executeJob(id);
    }

    private DesensitizationJobResponse executeJob(Long id) {
        DesensitizationJobEntity entity = desensitizationJobMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("DesensitizationJob not found: " + id);
        }

        entity.setStatus("RUNNING");
        entity.setExecuteAt(LocalDateTime.now());
        desensitizationJobMapper.updateById(entity);

        try {
            // Simulate job execution
            String result = "Job executed successfully";
            entity.setStatus("COMPLETED");
            entity.setCompletedAt(LocalDateTime.now());
            entity.setResult(result);
            log.info("Job {} completed successfully", id);
        } catch (Exception e) {
            entity.setStatus("FAILED");
            entity.setCompletedAt(LocalDateTime.now());
            entity.setResult("Execution failed: " + e.getMessage());
            log.error("Job {} failed", id, e);
        }

        desensitizationJobMapper.updateById(entity);
        return toResponse(entity);
    }

    private DesensitizationJobResponse toResponse(DesensitizationJobEntity entity) {
        return DesensitizationJobResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .sourceId(entity.getSourceId())
                .targetId(entity.getTargetId())
                .ruleIds(entity.getRuleIds())
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
