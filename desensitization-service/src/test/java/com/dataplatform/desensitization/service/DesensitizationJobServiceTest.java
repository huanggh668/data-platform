package com.dataplatform.desensitization.service;

import com.dataplatform.desensitization.dto.DesensitizationJobRequest;
import com.dataplatform.desensitization.dto.DesensitizationJobResponse;
import com.dataplatform.desensitization.entity.DesensitizationJobEntity;
import com.dataplatform.desensitization.mapper.DesensitizationJobMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DesensitizationJobService 单元测试
 *
 * @author system
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DesensitizationJobService 单元测试")
class DesensitizationJobServiceTest {

    @Mock
    private DesensitizationJobMapper desensitizationJobMapper;

    @InjectMocks
    private DesensitizationJobService desensitizationJobService;

    private DesensitizationJobEntity existingJob;

    @BeforeEach
    void setUp() {
        existingJob = new DesensitizationJobEntity();
        existingJob.setId(1L);
        existingJob.setName("test-desensitize-job");
        existingJob.setSourceId(10L);
        existingJob.setTargetId(20L);
        existingJob.setRuleIds("1,2,3");
        existingJob.setStatus("PENDING");
        existingJob.setIsScheduled(false);
        existingJob.setCreatedAt(LocalDateTime.now());
        existingJob.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== 创建 ====================

    @Test
    @DisplayName("create - 创建任务成功，状态初始化为 PENDING")
    void create_success() {
        DesensitizationJobRequest request = new DesensitizationJobRequest();
        request.setName("new-job");
        request.setSourceId(5L);
        request.setTargetId(6L);
        request.setRuleIds("1,2");
        request.setIsScheduled(false);

        when(desensitizationJobMapper.insert(any(DesensitizationJobEntity.class))).thenReturn(1);

        DesensitizationJobResponse response = desensitizationJobService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("new-job");
        assertThat(response.getStatus()).isEqualTo("PENDING");

        ArgumentCaptor<DesensitizationJobEntity> captor = ArgumentCaptor.forClass(DesensitizationJobEntity.class);
        verify(desensitizationJobMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
        assertThat(captor.getValue().getIsScheduled()).isFalse();
    }

    // ==================== 更新 ====================

    @Test
    @DisplayName("update - 任务存在时更新成功")
    void update_success() {
        when(desensitizationJobMapper.selectById(1L)).thenReturn(existingJob);
        when(desensitizationJobMapper.updateById(any(DesensitizationJobEntity.class))).thenReturn(1);

        DesensitizationJobRequest request = new DesensitizationJobRequest();
        request.setName("updated-job");
        request.setSourceId(11L);
        request.setTargetId(22L);
        request.setRuleIds("4,5");
        request.setIsScheduled(true);

        DesensitizationJobResponse response = desensitizationJobService.update(1L, request);

        assertThat(response.getName()).isEqualTo("updated-job");
        assertThat(response.getRuleIds()).isEqualTo("4,5");
        verify(desensitizationJobMapper).updateById(any(DesensitizationJobEntity.class));
    }

    @Test
    @DisplayName("update - 任务不存在时抛出 RuntimeException")
    void update_notFound() {
        when(desensitizationJobMapper.selectById(999L)).thenReturn(null);

        DesensitizationJobRequest request = new DesensitizationJobRequest();
        request.setName("x");

        assertThatThrownBy(() -> desensitizationJobService.update(999L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(desensitizationJobMapper, never()).updateById(any());
    }

    // ==================== 删除 ====================

    @Test
    @DisplayName("delete - 调用 deleteById")
    void delete_callsMapper() {
        desensitizationJobService.delete(1L);
        verify(desensitizationJobMapper).deleteById(1L);
    }

    // ==================== 查询 ====================

    @Test
    @DisplayName("getById - 任务存在时返回响应")
    void getById_found() {
        when(desensitizationJobMapper.selectById(1L)).thenReturn(existingJob);

        DesensitizationJobResponse response = desensitizationJobService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("getById - 任务不存在时返回 null")
    void getById_notFound() {
        when(desensitizationJobMapper.selectById(999L)).thenReturn(null);

        DesensitizationJobResponse response = desensitizationJobService.getById(999L);

        assertThat(response).isNull();
    }

    @Test
    @DisplayName("listAll - 返回任务列表")
    void listAll_returnsList() {
        when(desensitizationJobMapper.selectList(any())).thenReturn(
                java.util.Arrays.asList(existingJob)
        );

        java.util.List<DesensitizationJobResponse> list = desensitizationJobService.listAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("test-desensitize-job");
    }

    // ==================== 执行 ====================

    @Test
    @DisplayName("execute - 任务存在时执行成功，状态变为 COMPLETED")
    void execute_success() {
        when(desensitizationJobMapper.selectById(1L)).thenReturn(existingJob);
        when(desensitizationJobMapper.updateById(any(DesensitizationJobEntity.class))).thenReturn(1);

        DesensitizationJobResponse response = desensitizationJobService.execute(1L);

        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        verify(desensitizationJobMapper, atLeast(2)).updateById(any(DesensitizationJobEntity.class));
    }

    @Test
    @DisplayName("execute - 任务不存在时抛出 RuntimeException")
    void execute_notFound() {
        when(desensitizationJobMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> desensitizationJobService.execute(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
}
