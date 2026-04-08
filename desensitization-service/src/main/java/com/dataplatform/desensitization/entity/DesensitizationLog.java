package com.dataplatform.desensitization.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("DESENSITIZATION_LOG")
public class DesensitizationLog {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String dataType;

    private String originalValue;

    private String maskedValue;

    private String operationType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}