package com.dataplatform.watermark.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dataplatform.watermark.algorithm.ImageWatermarkAlgorithm;
import com.dataplatform.watermark.algorithm.TextWatermarkAlgorithm;
import com.dataplatform.watermark.mapper.WatermarkRecordMapper;
import com.dataplatform.watermark.model.WatermarkData;
import com.dataplatform.watermark.model.WatermarkRecord;
import com.dataplatform.watermark.model.WatermarkResult;
import com.dataplatform.watermark.model.WatermarkVerificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatermarkService {
    
    private final TextWatermarkAlgorithm textWatermarkAlgorithm;
    private final ImageWatermarkAlgorithm imageWatermarkAlgorithm;
    private final WatermarkRecordMapper watermarkRecordMapper;
    
    public WatermarkResult embedTextWatermark(String text, Long userId, String dataType) {
        WatermarkData watermarkData = WatermarkData.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .dataType(dataType)
                .build();
        
        String watermarkedText = textWatermarkAlgorithm.embed(text, watermarkData);
        
        WatermarkResult result = WatermarkResult.of(text, watermarkedText, watermarkData);
        
        saveWatermarkRecord(text, "text", watermarkedText, "embedded", userId);
        
        return result;
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
        
        WatermarkResult result = WatermarkResult.builder()
                .originalData(originalBase64)
                .watermarkedData(watermarkedBase64)
                .watermarkInfo(watermarkData)
                .timestamp(System.currentTimeMillis())
                .build();
        
        saveWatermarkRecord(originalBase64, "image", watermarkedBase64, "embedded", userId);
        
        return result;
    }
    
    public WatermarkData extractWatermark(String watermarkedText) {
        WatermarkData extracted = textWatermarkAlgorithm.extract(watermarkedText);
        if (extracted != null) {
            saveWatermarkRecord(watermarkedText, "text", null, "extracted", extracted.getUserId());
        }
        return extracted;
    }
    
    public WatermarkData extractImageWatermark(byte[] watermarkedImage) throws IOException {
        WatermarkData extracted = imageWatermarkAlgorithm.extract(watermarkedImage);
        if (extracted != null) {
            String imageBase64 = Base64.getEncoder().encodeToString(watermarkedImage);
            saveWatermarkRecord(imageBase64, "image", null, "extracted", extracted.getUserId());
        }
        return extracted;
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
    
    public List<WatermarkRecord> queryWatermarkRecords(Long userId) {
        LambdaQueryWrapper<WatermarkRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(WatermarkRecord::getId, userId);
        }
        wrapper.orderByDesc(WatermarkRecord::getEmbeddedAt);
        return watermarkRecordMapper.selectList(wrapper);
    }
    
    public WatermarkRecord queryWatermarkRecordById(Long id) {
        return watermarkRecordMapper.selectById(id);
    }
    
    private void saveWatermarkRecord(String fileName, String fileType, String watermarkContent, String status, Long userId) {
        try {
            WatermarkRecord record = new WatermarkRecord();
            record.setFileName(fileName);
            record.setFileType(fileType);
            record.setWatermarkContent(watermarkContent);
            record.setWatermarkPosition("embedded");
            record.setEmbeddedAt(LocalDateTime.now());
            record.setStatus(status);
            watermarkRecordMapper.insert(record);
            log.info("Watermark record saved: fileName={}, fileType={}, status={}", fileName, fileType, status);
        } catch (Exception e) {
            log.error("Failed to save watermark record", e);
        }
    }
}
