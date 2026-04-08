package com.dataplatform.watermark.service;

import com.dataplatform.watermark.algorithm.ImageWatermarkAlgorithm;
import com.dataplatform.watermark.algorithm.TextWatermarkAlgorithm;
import com.dataplatform.watermark.model.WatermarkData;
import com.dataplatform.watermark.model.WatermarkResult;
import com.dataplatform.watermark.model.WatermarkVerificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatermarkService {
    
    private final TextWatermarkAlgorithm textWatermarkAlgorithm;
    private final ImageWatermarkAlgorithm imageWatermarkAlgorithm;
    
    public WatermarkResult embedTextWatermark(String text, Long userId, String dataType) {
        WatermarkData watermarkData = WatermarkData.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .dataType(dataType)
                .build();
        
        String watermarkedText = textWatermarkAlgorithm.embed(text, watermarkData);
        
        return WatermarkResult.of(text, watermarkedText, watermarkData);
    }
    
    public WatermarkResult embedImageWatermark(byte[] image, Long userId, String dataType) throws IOException {
        WatermarkData watermarkData = WatermarkData.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .dataType(dataType)
                .build();
        
        byte[] watermarkedImage = imageWatermarkAlgorithm.embed(image, watermarkData);
        String originalBase64 = Base64.getEncoder().encodeToString(image);
        String watermarkedBase64 = Base64.getEncoder().encodeToString(watermarkedImage);
        
        return WatermarkResult.builder()
                .originalData(originalBase64)
                .watermarkedData(watermarkedBase64)
                .watermarkInfo(watermarkData)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public WatermarkData extractWatermark(String watermarkedText) {
        return textWatermarkAlgorithm.extract(watermarkedText);
    }
    
    public WatermarkData extractImageWatermark(byte[] watermarkedImage) throws IOException {
        return imageWatermarkAlgorithm.extract(watermarkedImage);
    }
    
    public WatermarkVerificationResult verifyTextWatermark(String text) {
        try {
            WatermarkData extracted = textWatermarkAlgorithm.extract(text);
            if (extracted != null) {
                return WatermarkVerificationResult.success(extracted);
            }
            return WatermarkVerificationResult.failure("No watermark found in text");
        } catch (Exception e) {
            log.error("Text watermark verification error", e);
            return WatermarkVerificationResult.failure("Verification error: " + e.getMessage());
        }
    }
    
    public WatermarkVerificationResult verifyImageWatermark(byte[] image) {
        try {
            WatermarkData extracted = imageWatermarkAlgorithm.extract(image);
            if (extracted != null) {
                return WatermarkVerificationResult.success(extracted);
            }
            return WatermarkVerificationResult.failure("No watermark found in image");
        } catch (Exception e) {
            log.error("Image watermark verification error", e);
            return WatermarkVerificationResult.failure("Verification error: " + e.getMessage());
        }
    }
}
