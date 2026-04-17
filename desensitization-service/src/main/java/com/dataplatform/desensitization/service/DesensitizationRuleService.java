package com.dataplatform.desensitization.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataplatform.desensitization.dto.DesensitizationRuleRequest;
import com.dataplatform.desensitization.dto.DesensitizationRuleResponse;
import com.dataplatform.desensitization.entity.DesensitizationRuleEntity;
import com.dataplatform.desensitization.mapper.DesensitizationRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesensitizationRuleService {

    private final DesensitizationRuleMapper desensitizationRuleMapper;

    public DesensitizationRuleResponse create(DesensitizationRuleRequest request) {
        DesensitizationRuleEntity entity = new DesensitizationRuleEntity();
        entity.setName(request.getName());
        entity.setDataType(request.getDataType());
        entity.setPattern(request.getPattern());
        entity.setReplacement(request.getReplacement());
        entity.setPriority(request.getPriority());
        entity.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        desensitizationRuleMapper.insert(entity);
        log.info("Created desensitization rule: {}", entity.getId());
        return toResponse(entity);
    }

    public DesensitizationRuleResponse update(Long id, DesensitizationRuleRequest request) {
        DesensitizationRuleEntity entity = desensitizationRuleMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("DesensitizationRule not found: " + id);
        }
        entity.setName(request.getName());
        entity.setDataType(request.getDataType());
        entity.setPattern(request.getPattern());
        entity.setReplacement(request.getReplacement());
        entity.setPriority(request.getPriority());
        entity.setEnabled(request.getEnabled());
        desensitizationRuleMapper.updateById(entity);
        log.info("Updated desensitization rule: {}", id);
        return toResponse(entity);
    }

    public void delete(Long id) {
        desensitizationRuleMapper.deleteById(id);
        log.info("Deleted desensitization rule: {}", id);
    }

    public DesensitizationRuleResponse getById(Long id) {
        DesensitizationRuleEntity entity = desensitizationRuleMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return toResponse(entity);
    }

    public List<DesensitizationRuleResponse> listAll() {
        LambdaQueryWrapper<DesensitizationRuleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DesensitizationRuleEntity::getPriority)
               .orderByDesc(DesensitizationRuleEntity::getCreatedAt);
        return desensitizationRuleMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DesensitizationRuleResponse> listEnabled() {
        LambdaQueryWrapper<DesensitizationRuleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DesensitizationRuleEntity::getEnabled, true)
               .orderByAsc(DesensitizationRuleEntity::getPriority);
        return desensitizationRuleMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private DesensitizationRuleResponse toResponse(DesensitizationRuleEntity entity) {
        return DesensitizationRuleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .dataType(entity.getDataType())
                .pattern(entity.getPattern())
                .replacement(entity.getReplacement())
                .priority(entity.getPriority())
                .enabled(entity.getEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
