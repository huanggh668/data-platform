package com.dataplatform.watermark.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dataplatform.watermark.dto.WatermarkFactorRequest;
import com.dataplatform.watermark.dto.WatermarkFactorResponse;
import com.dataplatform.watermark.entity.WatermarkFactorEntity;
import com.dataplatform.watermark.mapper.WatermarkFactorMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatermarkFactorService {
    private final WatermarkFactorMapper watermarkFactorMapper;

    public WatermarkFactorResponse create(WatermarkFactorRequest request) {
        WatermarkFactorEntity entity = new WatermarkFactorEntity();
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setValue(request.getValue());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkFactorMapper.insert(entity);
        log.info("Created watermark factor: id={}, name={}", entity.getId(), entity.getName());
        return toResponse(entity);
    }

    public WatermarkFactorResponse update(Long id, WatermarkFactorRequest request) {
        WatermarkFactorEntity entity = watermarkFactorMapper.selectById(id);
        if (entity == null) {
            log.warn("Watermark factor not found: id={}", id);
            return null;
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getType() != null) {
            entity.setType(request.getType());
        }
        if (request.getValue() != null) {
            entity.setValue(request.getValue());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        watermarkFactorMapper.updateById(entity);
        log.info("Updated watermark factor: id={}", id);
        return toResponse(entity);
    }

    public void delete(Long id) {
        watermarkFactorMapper.deleteById(id);
        log.info("Deleted watermark factor: id={}", id);
    }

    public WatermarkFactorResponse getById(Long id) {
        WatermarkFactorEntity entity = watermarkFactorMapper.selectById(id);
        return entity != null ? toResponse(entity) : null;
    }

    public List<WatermarkFactorResponse> list() {
        LambdaQueryWrapper<WatermarkFactorEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(WatermarkFactorEntity::getCreatedAt);
        return watermarkFactorMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private WatermarkFactorResponse toResponse(WatermarkFactorEntity entity) {
        return WatermarkFactorResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .value(entity.getValue())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}