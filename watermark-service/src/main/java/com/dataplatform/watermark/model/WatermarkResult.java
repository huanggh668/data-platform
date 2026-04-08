package com.dataplatform.watermark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkResult {
    
    private String originalData;
    private String watermarkedData;
    private WatermarkData watermarkInfo;
    private Long timestamp;
    
    public static WatermarkResult of(String original, String watermarked, WatermarkData watermarkInfo) {
        return WatermarkResult.builder()
                .originalData(original)
                .watermarkedData(watermarked)
                .watermarkInfo(watermarkInfo)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
