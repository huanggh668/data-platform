package com.dataplatform.watermark.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WatermarkJobRequest {
    private String name;
    private String type;
    private String factorIds;
    private String sourcePath;
    private String targetPath;
    private String status;
    private String cronExpression;
    private Boolean isScheduled;
    private LocalDateTime executeAt;
}