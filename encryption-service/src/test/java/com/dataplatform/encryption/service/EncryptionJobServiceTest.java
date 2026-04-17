package com.dataplatform.encryption.service;

import com.dataplatform.encryption.dto.EncryptionJobRequest;
import com.dataplatform.encryption.dto.EncryptionJobResponse;
import com.dataplatform.encryption.entity.EncryptionJobEntity;
import com.dataplatform.encryption.mapper.EncryptionJobMapper;
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
 * EncryptionJobService 单元测试
 *
 * @author system
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EncryptionJobService 单元测试")
class EncryptionJobServiceTest {

    @Mock
    private EncryptionJobMapper encryptionJobMapper;

    @InjectMocks
    private EncryptionJobService encryptionJobService;

    private EncryptionJobEntity existingJob;

    @BeforeEach
    void setUp() {
        existingJob = new EncryptionJobEntity();
        existingJob.setId(1L);
        existingJob.setName("test-job");
        existingJob.setAlgorithmId(1L);
        existingJob.setSourcePath("/data/input");
        existingJob.setTargetPath("/data/output");
        existingJob.setOperation("ENCRYPT");
        existingJob.setStatus("PENDING");
        existingJob.setIsScheduled(false);
        existingJob.setCreatedAt(LocalDateTime.now());
        existingJob.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== 创建 ====================

    @Test
    @DisplayName("create - 创建任务成功，状态初始化为 PENDING")
    void create_success() {
        EncryptionJobRequest request = new EncryptionJobRequest();
        request.setName("new-job");
        request.setAlgorithmId(2L);
        request.setSourcePath("/src");
        request.setTargetPath("/dst");
        request.setOperation("DECRYPT");
        request.setIsScheduled(false);

        when(encryptionJobMapper.insert(any(EncryptionJobEntity.class))).thenReturn(1);

        EncryptionJobResponse response = encryptionJobService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("new-job");
        assertThat(response.getStatus()).isEqualTo("PENDING");

        ArgumentCaptor<EncryptionJobEntity> captor = ArgumentCaptor.forClass(EncryptionJobEntity.class);
        verify(encryptionJobMapper).insert(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
    }

    // ==================== 更新 ====================

    @Test
    @DisplayName("update - 任务存在时更新成功")
    void update_success() {
        when(encryptionJobMapper.selectById(1L)).thenReturn(existingJob);
        when(encryptionJobMapper.updateById(any(EncryptionJobEntity.class))).thenReturn(1);

        EncryptionJobRequest request = new EncryptionJobRequest();
        request.setName("updated-job");
        request.setAlgorithmId(3L);
        request.setSourcePath("/new-src");
        request.setTargetPath("/new-dst");
        request.setOperation("ENCRYPT");
        request.setIsScheduled(true);

        EncryptionJobResponse response = encryptionJobService.update(1L, request);

        assertThat(response.getName()).isEqualTo("updated-job");
        verify(encryptionJobMapper).updateById(any(EncryptionJobEntity.class));
    }

    @Test
    @DisplayName("update - 任务不存在时抛出 IllegalArgumentException")
    void update_notFound() {
        when(encryptionJobMapper.selectById(999L)).thenReturn(null);

        EncryptionJobRequest request = new EncryptionJobRequest();
        request.setName("x");

        assertThatThrownBy(() -> encryptionJobService.update(999L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");

        verify(encryptionJobMapper, never()).updateById(any());
    }

    // ==================== 删除 ====================

    @Test
    @DisplayName("delete - 调用 deleteById")
    void delete_callsMapper() {
        encryptionJobService.delete(1L);
        verify(encryptionJobMapper).deleteById(1L);
    }

    // ==================== 查询 ====================

    @Test
    @DisplayName("getById - 任务存在时返回响应")
    void getById_found() {
        when(encryptionJobMapper.selectById(1L)).thenReturn(existingJob);

        EncryptionJobResponse response = encryptionJobService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("test-job");
    }

    @Test
    @DisplayName("getById - 任务不存在时返回 null")
    void getById_notFound() {
        when(encryptionJobMapper.selectById(999L)).thenReturn(null);

        EncryptionJobResponse response = encryptionJobService.getById(999L);

        assertThat(response).isNull();
    }

    // ==================== 执行 ====================

    @Test
    @DisplayName("execute - 任务存在时执行成功，状态变为 COMPLETED")
    void execute_success() {
        when(encryptionJobMapper.selectById(1L)).thenReturn(existingJob);
        when(encryptionJobMapper.updateById(any(EncryptionJobEntity.class))).thenReturn(1);

        EncryptionJobResponse response = encryptionJobService.execute(1L);

        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        verify(encryptionJobMapper, atLeast(2)).updateById(any(EncryptionJobEntity.class));
    }

    @Test
    @DisplayName("execute - 任务不存在时抛出 IllegalArgumentException")
    void execute_notFound() {
        when(encryptionJobMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> encryptionJobService.execute(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}
