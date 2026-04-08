package com.dataplatform.watermark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkVerificationResult {
    
    private boolean valid;
    private WatermarkData watermarkData;
    private String message;
    
    public static WatermarkVerificationResult success(WatermarkData data) {
        return WatermarkVerificationResult.builder()
                .valid(true)
                .watermarkData(data)
                .message("Watermark verified successfully")
                .build();
    }
    
    public static WatermarkVerificationResult failure(String message) {
        return WatermarkVerificationResult.builder()
                .valid(false)
                .message(message)
                .build();
    }
    
    public static WatermarkVerificationResult failure() {
        return WatermarkVerificationResult.builder()
                .valid(false)
                .message("Watermark verification failed")
                .build();
    }
}
