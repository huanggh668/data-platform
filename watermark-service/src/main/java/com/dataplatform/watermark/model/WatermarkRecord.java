package com.dataplatform.watermark.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("WATERMARK_RECORD")
public class WatermarkRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String fileType;

    private String watermarkContent;

    private String watermarkPosition;

    private LocalDateTime embeddedAt;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}