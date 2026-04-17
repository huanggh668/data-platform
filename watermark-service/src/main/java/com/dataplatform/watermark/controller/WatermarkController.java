package com.dataplatform.watermark.controller;

import com.dataplatform.watermark.dto.*;
import com.dataplatform.watermark.model.WatermarkData;
import com.dataplatform.watermark.model.WatermarkRecord;
import com.dataplatform.watermark.model.WatermarkResult;
import com.dataplatform.watermark.model.WatermarkVerificationResult;
import com.dataplatform.watermark.service.WatermarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Tag(name = "水印核心操作", description = "文本/图片水印嵌入、提取、验证及溯源记录查询")
@RestController
@RequestMapping("/api/v1/watermark")
@RequiredArgsConstructor
public class WatermarkController {

    private final WatermarkService watermarkService;

    @Operation(summary = "嵌入文本水印", description = "将用户信息嵌入文本数据中")
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

    @Operation(summary = "嵌入图片水印", description = "图片数据以 Base64 编码传入")
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

    @Operation(summary = "提取水印", description = "从文本或图片中提取嵌入的水印信息；type=image 时 dataBase64 传图片Base64")
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

    @Operation(summary = "验证水印", description = "验证文本或图片中是否包含合法水印")
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

    @Operation(summary = "查询水印溯源记录", description = "按用户ID过滤溯源记录，不传则查全部")
    @GetMapping("/records")
    public ResponseEntity<List<WatermarkRecord>> queryWatermarkRecords(
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        List<WatermarkRecord> records = watermarkService.queryWatermarkRecords(userId);
        return ResponseEntity.ok(records);
    }

    @Operation(summary = "根据ID查询溯源记录")
    @GetMapping("/records/{id}")
    public ResponseEntity<WatermarkRecord> getWatermarkRecordById(
            @Parameter(description = "记录ID") @PathVariable Long id) {
        WatermarkRecord record = watermarkService.queryWatermarkRecordById(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }
}
