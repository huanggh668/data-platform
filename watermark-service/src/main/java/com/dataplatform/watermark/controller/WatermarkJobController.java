package com.dataplatform.watermark.controller;

import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.watermark.dto.WatermarkJobRequest;
import com.dataplatform.watermark.dto.WatermarkJobResponse;
import com.dataplatform.watermark.service.WatermarkJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "水印任务管理", description = "水印嵌入/提取任务的创建、执行及进度查询接口")
@RestController
@RequestMapping("/api/v1/watermark/jobs")
@RequiredArgsConstructor
public class WatermarkJobController {
    private final WatermarkJobService watermarkJobService;

    @Operation(summary = "创建水印任务")
    @PostMapping
    public ApiResponse<WatermarkJobResponse> create(@RequestBody WatermarkJobRequest request) {
        WatermarkJobResponse response = watermarkJobService.create(request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "更新水印任务")
    @PutMapping("/{id}")
    public ApiResponse<WatermarkJobResponse> update(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestBody WatermarkJobRequest request) {
        WatermarkJobResponse response = watermarkJobService.update(id, request);
        if (response == null) {
            return ApiResponse.notFound("Job not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "删除水印任务")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "任务ID") @PathVariable Long id) {
        watermarkJobService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "根据ID查询水印任务")
    @GetMapping("/{id}")
    public ApiResponse<WatermarkJobResponse> getById(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        WatermarkJobResponse response = watermarkJobService.getById(id);
        if (response == null) {
            return ApiResponse.notFound("Job not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "查询所有水印任务")
    @GetMapping
    public ApiResponse<List<WatermarkJobResponse>> list() {
        List<WatermarkJobResponse> responses = watermarkJobService.list();
        return ApiResponse.success(responses);
    }

    @Operation(summary = "执行水印任务", description = "触发任务立即执行，任务状态变为 RUNNING")
    @PostMapping("/{id}/execute")
    public ApiResponse<WatermarkJobResponse> execute(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        WatermarkJobResponse response = watermarkJobService.execute(id);
        if (response == null) {
            return ApiResponse.notFound("Job not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "查询任务执行进度", description = "返回任务当前进度（0-100）、状态及错误信息")
    @GetMapping("/{id}/progress")
    public ApiResponse<WatermarkJobResponse> getProgress(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        WatermarkJobResponse response = watermarkJobService.getById(id);
        if (response == null) {
            return ApiResponse.notFound("Job not found");
        }
        return ApiResponse.success(response);
    }
}
