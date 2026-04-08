package com.dataplatform.encryption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ENCRYPTION_LOG")
public class EncryptionLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String algorithm;

    private String operation;

    private String originalData;

    private String encryptedData;

    private String keyId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private String status;
}