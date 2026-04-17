package com.dataplatform.encryption.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("encryption_job")
public class EncryptionJobEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private Long algorithmId;

    private String sourcePath;

    private String targetPath;

    private String operation;

    private String status;

    /** 任务执行进度 0-100，执行中实时更新 */
    private Integer progress;

    /** 任务错误信息，执行失败时写入 */
    private String errorMessage;

    private String cronExpression;

    private Boolean isScheduled;

    private LocalDateTime executeAt;

    private LocalDateTime completedAt;

    private String result;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}