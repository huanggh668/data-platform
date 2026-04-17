package com.dataplatform.watermark.service;

import com.dataplatform.watermark.dto.WatermarkJobRequest;
import com.dataplatform.watermark.dto.WatermarkJobResponse;
import com.dataplatform.watermark.entity.WatermarkJobEntity;
import com.dataplatform.watermark.mapper.WatermarkJobMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * WatermarkJobService 单元测试
 *
 * @author system
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WatermarkJobService 单元测试")
class WatermarkJobServiceTest {

    @Mock
    private WatermarkJobMapper watermarkJobMapper;

    @InjectMocks
    private WatermarkJobService watermarkJobService;

    private WatermarkJobEntity existingJob;

    @BeforeEach
    void setUp() {
        existingJob = new WatermarkJobEntity();
        existingJob.setId(1L);
        existingJob.setName("test-watermark-job");
        existingJob.setType("TEXT");
        existingJob.setFactorIds("1,2");
        existingJob.setSourcePath("/src/data.txt");
        existingJob.setTargetPath("/dst/data.txt");
        existingJob.setStatus("PENDING");
        existingJob.setIsScheduled(false);
        existingJob.setDeleted(0);
        existingJob.setCreatedAt(LocalDateTime.now());
        existingJob.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== 创建 ====================

    @Test
    @DisplayName("create - 创建任务成功，状态初始化为 PENDING")
    void create_success() {
        WatermarkJobRequest request = new WatermarkJobRequest();
        request.setName("new-watermark-job");
        request.setType("IMAGE");
        request.setFactorIds("3,4");
        request.setSourcePath("/in/img.png");
        request.setTargetPath("/out/img.png");
        request.setIsScheduled(false);

        when(watermarkJobMapper.insert(any(WatermarkJobEntity.class))).thenReturn(1);

        WatermarkJobResponse response = watermarkJobService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("new-watermark-job");
        assertThat(response.getStatus()).isEqualTo("PENDING");

        ArgumentCaptor<WatermarkJobEntity> captor = ArgumentCaptor.forClass(WatermarkJobEntity.class);
        verify(watermarkJobMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
        assertThat(captor.getValue().getIsScheduled()).isFalse();
    }

    // ==================== 更新 ====================

    @Test
    @DisplayName("update - 任务存在时更新成功")
    void update_success() {
        when(watermarkJobMapper.selectById(1L)).thenReturn(existingJob);
        when(watermarkJobMapper.updateById(any(WatermarkJobEntity.class))).thenReturn(1);

        WatermarkJobRequest request = new WatermarkJobRequest();
        request.setName("updated-job");
        request.setType("TEXT");
        request.setSourcePath("/new/src");

        WatermarkJobResponse response = watermarkJobService.update(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("updated-job");
        verify(watermarkJobMapper).updateById(any(WatermarkJobEntity.class));
    }

    @Test
    @DisplayName("update - 任务不存在时返回 null")
    void update_notFound() {
        when(watermarkJobMapper.selectById(999L)).thenReturn(null);

        WatermarkJobRequest request = new WatermarkJobRequest();
        request.setName("x");

        WatermarkJobResponse response = watermarkJobService.update(999L, request);

        assertThat(response).isNull();
        verify(watermarkJobMapper, never()).updateById(any());
    }

    // ==================== 删除 ====================

    @Test
    @DisplayName("delete - 调用 deleteById")
    void delete_callsMapper() {
        watermarkJobService.delete(1L);
        verify(watermarkJobMapper).deleteById(1L);
    }

    // ==================== 查询 ====================

    @Test
    @DisplayName("getById - 任务存在时返回响应")
    void getById_found() {
        when(watermarkJobMapper.selectById(1L)).thenReturn(existingJob);

        WatermarkJobResponse response = watermarkJobService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("test-watermark-job");
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("getById - 任务不存在时返回 null")
    void getById_notFound() {
        when(watermarkJobMapper.selectById(999L)).thenReturn(null);

        WatermarkJobResponse response = watermarkJobService.getById(999L);

        assertThat(response).isNull();
    }

    @Test
    @DisplayName("list - 返回所有任务列表")
    void list_returnsList() {
        WatermarkJobEntity job2 = new WatermarkJobEntity();
        job2.setId(2L);
        job2.setName("job-2");
        job2.setStatus("COMPLETED");
        job2.setIsScheduled(false);
        job2.setDeleted(0);
        job2.setCreatedAt(LocalDateTime.now());
        job2.setUpdatedAt(LocalDateTime.now());

        when(watermarkJobMapper.selectList(any())).thenReturn(Arrays.asList(existingJob, job2));

        List<WatermarkJobResponse> list = watermarkJobService.list();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getName()).isEqualTo("test-watermark-job");
        assertThat(list.get(1).getName()).isEqualTo("job-2");
    }
}
