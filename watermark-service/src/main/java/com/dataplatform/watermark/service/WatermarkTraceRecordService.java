package com.dataplatform.watermark.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataplatform.watermark.dto.WatermarkTraceRecordRequest;
import com.dataplatform.watermark.dto.WatermarkTraceRecordResponse;
import com.dataplatform.watermark.entity.WatermarkTraceRecordEntity;
import com.dataplatform.watermark.mapper.WatermarkTraceRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatermarkTraceRecordService {
    private final WatermarkTraceRecordMapper watermarkTraceRecordMapper;

    public WatermarkTraceRecordResponse create(WatermarkTraceRecordRequest request) {
        WatermarkTraceRecordEntity entity = new WatermarkTraceRecordEntity();
        entity.setJobId(request.getJobId());
        entity.setFactorId(request.getFactorId());
        entity.setEmbeddedData(request.getEmbeddedData());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkTraceRecordMapper.insert(entity);
        log.info("Created watermark trace record: id={}, jobId={}", entity.getId(), entity.getJobId());
        return toResponse(entity);
    }

    public WatermarkTraceRecordResponse update(Long id, WatermarkTraceRecordRequest request) {
        WatermarkTraceRecordEntity entity = watermarkTraceRecordMapper.selectById(id);
        if (entity == null) {
            log.warn("Watermark trace record not found: id={}", id);
            return null;
        }
        if (request.getJobId() != null) {
            entity.setJobId(request.getJobId());
        }
        if (request.getFactorId() != null) {
            entity.setFactorId(request.getFactorId());
        }
        if (request.getEmbeddedData() != null) {
            entity.setEmbeddedData(request.getEmbeddedData());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkTraceRecordMapper.updateById(entity);
        log.info("Updated watermark trace record: id={}", id);
        return toResponse(entity);
    }

    public void delete(Long id) {
        watermarkTraceRecordMapper.deleteById(id);
        log.info("Deleted watermark trace record: id={}", id);
    }

    public WatermarkTraceRecordResponse getById(Long id) {
        WatermarkTraceRecordEntity entity = watermarkTraceRecordMapper.selectById(id);
        return entity != null ? toResponse(entity) : null;
    }

    public List<WatermarkTraceRecordResponse> list() {
        LambdaQueryWrapper<WatermarkTraceRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WatermarkTraceRecordEntity::getCreatedAt);
        return watermarkTraceRecordMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WatermarkTraceRecordResponse> searchByJobId(Long jobId) {
        LambdaQueryWrapper<WatermarkTraceRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WatermarkTraceRecordEntity::getJobId, jobId);
        wrapper.orderByDesc(WatermarkTraceRecordEntity::getCreatedAt);
        return watermarkTraceRecordMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WatermarkTraceRecordResponse> searchByFactorId(Long factorId) {
        LambdaQueryWrapper<WatermarkTraceRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WatermarkTraceRecordEntity::getFactorId, factorId);
        wrapper.orderByDesc(WatermarkTraceRecordEntity::getCreatedAt);
        return watermarkTraceRecordMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private WatermarkTraceRecordResponse toResponse(WatermarkTraceRecordEntity entity) {
        return WatermarkTraceRecordResponse.builder()
                .id(entity.getId())
                .jobId(entity.getJobId())
                .factorId(entity.getFactorId())
                .embeddedData(entity.getEmbeddedData())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}