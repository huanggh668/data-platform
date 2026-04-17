package com.dataplatform.watermark.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("watermark_trace_record")
public class WatermarkTraceRecordEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long jobId;
    
    private Long factorId;
    
    private String embeddedData;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;
}