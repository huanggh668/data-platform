package com.dataplatform.watermark.dto;

import lombok.Data;

@Data
public class WatermarkTraceRecordRequest {
    private Long jobId;
    private Long factorId;
    private String embeddedData;
}