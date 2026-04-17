package com.dataplatform.desensitization.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dataplatform.desensitization.dto.DataSourceRequest;
import com.dataplatform.desensitization.dto.DataSourceResponse;
import com.dataplatform.desensitization.entity.DataSource;
import com.dataplatform.desensitization.mapper.DataSourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSourceService {

    private final DataSourceMapper dataSourceMapper;

    public DataSourceResponse create(DataSourceRequest request) {
        DataSource dataSource = new DataSource();
        dataSource.setName(request.getName());
        dataSource.setType(request.getType());
        dataSource.setConfig(request.getConfig());
        dataSourceMapper.insert(dataSource);
        log.info("Created data source: {}", dataSource.getId());
        return toResponse(dataSource);
    }

    public DataSourceResponse update(Long id, DataSourceRequest request) {
        DataSource dataSource = dataSourceMapper.selectById(id);
        if (dataSource == null) {
            throw new RuntimeException("DataSource not found: " + id);
        }
        dataSource.setName(request.getName());
        dataSource.setType(request.getType());
        dataSource.setConfig(request.getConfig());
        dataSourceMapper.updateById(dataSource);
        log.info("Updated data source: {}", id);
        return toResponse(dataSource);
    }

    public void delete(Long id) {
        dataSourceMapper.deleteById(id);
        log.info("Deleted data source: {}", id);
    }

    public DataSourceResponse getById(Long id) {
        DataSource dataSource = dataSourceMapper.selectById(id);
        if (dataSource == null) {
            return null;
        }
        return toResponse(dataSource);
    }

    public List<DataSourceResponse> listAll() {
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DataSource::getCreatedAt);
        return dataSourceMapper.selectList(wrapper).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private DataSourceResponse toResponse(DataSource dataSource) {
        return DataSourceResponse.builder()
                .id(dataSource.getId())
                .name(dataSource.getName())
                .type(dataSource.getType())
                .config(dataSource.getConfig())
                .createdAt(dataSource.getCreatedAt())
                .updatedAt(dataSource.getUpdatedAt())
                .build();
    }
}
