package com.dataplatform.watermark.controller;

import com.dataplatform.watermark.dto.*;
import com.dataplatform.watermark.model.WatermarkData;
import com.dataplatform.watermark.model.WatermarkResult;
import com.dataplatform.watermark.model.WatermarkVerificationResult;
import com.dataplatform.watermark.service.WatermarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/watermark")
@RequiredArgsConstructor
public class WatermarkController {
    
    private final WatermarkService watermarkService;
    
    @PostMapping("/embed/text")
    public ResponseEntity<WatermarkResponse> embedTextWatermark(@RequestBody EmbedTextRequest request) {
        WatermarkResult result = watermarkService.embedTextWatermark(
                request.getText(),
                request.getUserId(),
                request.getDataType()
        );
        
        WatermarkResponse response = WatermarkResponse.builder()
                .original(result.getOriginalData())
                .watermarked(result.getWatermarkedData())
                .watermarkInfo(result.getWatermarkInfo())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/embed/image")
    public ResponseEntity<WatermarkResponse> embedImageWatermark(@RequestBody EmbedImageRequest request) throws IOException {
        byte[] imageData = Base64.getDecoder().decode(request.getImageBase64());
        WatermarkResult result = watermarkService.embedImageWatermark(
                imageData,
                request.getUserId(),
                request.getDataType()
        );
        
        WatermarkResponse response = WatermarkResponse.builder()
                .original(result.getOriginalData())
                .watermarked(result.getWatermarkedData())
                .watermarkInfo(result.getWatermarkInfo())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/extract")
    public ResponseEntity<ExtractResponse> extractWatermark(@RequestBody ExtractRequest request) throws IOException {
        WatermarkData watermarkData;
        
        if ("image".equalsIgnoreCase(request.getType())) {
            byte[] imageData = Base64.getDecoder().decode(request.getDataBase64());
            watermarkData = watermarkService.extractImageWatermark(imageData);
        } else {
            watermarkData = watermarkService.extractWatermark(request.getData());
        }
        
        ExtractResponse response = ExtractResponse.builder()
                .watermarkInfo(watermarkData)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verifyWatermark(@RequestBody VerifyRequest request) throws IOException {
        WatermarkVerificationResult result;
        
        if ("image".equalsIgnoreCase(request.getType())) {
            byte[] imageData = Base64.getDecoder().decode(request.getDataBase64());
            result = watermarkService.verifyImageWatermark(imageData);
        } else {
            result = watermarkService.verifyTextWatermark(request.getData());
        }
        
        VerifyResponse response = VerifyResponse.builder()
                .valid(result.isValid())
                .watermarkInfo(result.getWatermarkData())
                .message(result.getMessage())
                .build();
        
        return ResponseEntity.ok(response);
    }
    
}
