package com.dataplatform.watermark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkTraceRecordResponse {
    private Long id;
    private Long jobId;
    private Long factorId;
    private String embeddedData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}