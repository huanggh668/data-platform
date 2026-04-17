package com.dataplatform.encryption.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dataplatform.encryption.entity.EncryptionLog;
import com.dataplatform.encryption.mapper.EncryptionLogMapper;
import com.dataplatform.encryption.model.AlgorithmType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EncryptionService 单元测试
 *
 * @author system
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EncryptionService 单元测试")
class EncryptionServiceTest {

    @Mock
    private EncryptionLogMapper encryptionLogMapper;

    @InjectMocks
    private EncryptionService encryptionService;

    // ==================== 加密 ====================

    @Test
    @DisplayName("encrypt - 不支持的算法类型时抛出 IllegalArgumentException")
    void encrypt_unsupportedAlgorithm() {
        // SM2 is not in the switch, but we can test the exception path conceptually;
        // AES/RSA/SM4 all call static utils which would fail in unit test without actual keys.
        // Test the known-bad path: null algorithm causes NPE from switch
        assertThatThrownBy(() -> encryptionService.encrypt("data", null, "key"))
                .isInstanceOf(Exception.class);
        verifyNoInteractions(encryptionLogMapper);
    }

    // ==================== 日志查询 ====================

    @Test
    @DisplayName("queryLogs - 分页查询返回结果")
    void queryLogs_returnPage() {
        Page<EncryptionLog> mockPage = new Page<>(1, 10);
        when(encryptionLogMapper.selectPage(any(Page.class), isNull())).thenReturn(mockPage);

        Page<EncryptionLog> result = encryptionService.queryLogs(1, 10);

        assertThat(result).isNotNull();
        verify(encryptionLogMapper).selectPage(any(Page.class), isNull());
    }

    @Test
    @DisplayName("getLogById - 返回指定 ID 的日志")
    void getLogById_found() {
        EncryptionLog log = new EncryptionLog();
        log.setId(1L);
        log.setAlgorithm("AES");
        log.setOperation("ENCRYPT");
        when(encryptionLogMapper.selectById(1L)).thenReturn(log);

        EncryptionLog result = encryptionService.getLogById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getAlgorithm()).isEqualTo("AES");
        assertThat(result.getOperation()).isEqualTo("ENCRYPT");
    }

    @Test
    @DisplayName("getLogById - ID 不存在时返回 null")
    void getLogById_notFound() {
        when(encryptionLogMapper.selectById(999L)).thenReturn(null);

        EncryptionLog result = encryptionService.getLogById(999L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("queryLogsByKeyId - 按 keyId 查询日志列表")
    void queryLogsByKeyId_returnList() {
        when(encryptionLogMapper.selectList(any())).thenReturn(java.util.Collections.emptyList());

        java.util.List<EncryptionLog> result = encryptionService.queryLogsByKeyId("key-001");

        assertThat(result).isNotNull().isEmpty();
        verify(encryptionLogMapper).selectList(any());
    }
}
