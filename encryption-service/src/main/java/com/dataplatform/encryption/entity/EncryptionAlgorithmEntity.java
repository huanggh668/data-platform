package com.dataplatform.encryption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("encryption_algorithm")
public class EncryptionAlgorithmEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String type;

    private Integer keyLength;

    private String mode;

    private String padding;

    private String config;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}