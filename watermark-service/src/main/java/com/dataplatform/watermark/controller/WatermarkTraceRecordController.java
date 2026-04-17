package com.dataplatform.watermark.controller;

import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.watermark.dto.WatermarkTraceRecordRequest;
import com.dataplatform.watermark.dto.WatermarkTraceRecordResponse;
import com.dataplatform.watermark.service.WatermarkTraceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "溯源记录管理", description = "水印溯源记录的增删改查及按任务ID/因子ID查询")
@RestController
@RequestMapping("/api/v1/watermark/trace-records")
@RequiredArgsConstructor
public class WatermarkTraceRecordController {
    private final WatermarkTraceRecordService watermarkTraceRecordService;

    @Operation(summary = "创建溯源记录")
    @PostMapping
    public ApiResponse<WatermarkTraceRecordResponse> create(@RequestBody WatermarkTraceRecordRequest request) {
        WatermarkTraceRecordResponse response = watermarkTraceRecordService.create(request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "更新溯源记录")
    @PutMapping("/{id}")
    public ApiResponse<WatermarkTraceRecordResponse> update(
            @Parameter(description = "记录ID") @PathVariable Long id,
            @RequestBody WatermarkTraceRecordRequest request) {
        WatermarkTraceRecordResponse response = watermarkTraceRecordService.update(id, request);
        if (response == null) {
            return ApiResponse.notFound("Trace record not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "删除溯源记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "记录ID") @PathVariable Long id) {
        watermarkTraceRecordService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "根据ID查询溯源记录")
    @GetMapping("/{id}")
    public ApiResponse<WatermarkTraceRecordResponse> getById(
            @Parameter(description = "记录ID") @PathVariable Long id) {
        WatermarkTraceRecordResponse response = watermarkTraceRecordService.getById(id);
        if (response == null) {
            return ApiResponse.notFound("Trace record not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "查询溯源记录列表", description = "可按任务ID(jobId)或因子ID(factorId)过滤，不传则返回全部")
    @GetMapping
    public ApiResponse<List<WatermarkTraceRecordResponse>> list(
            @Parameter(description = "任务ID（可选）") @RequestParam(required = false) Long jobId,
            @Parameter(description = "因子ID（可选）") @RequestParam(required = false) Long factorId) {
        List<WatermarkTraceRecordResponse> responses;
        if (jobId != null) {
            responses = watermarkTraceRecordService.searchByJobId(jobId);
        } else if (factorId != null) {
            responses = watermarkTraceRecordService.searchByFactorId(factorId);
        } else {
            responses = watermarkTraceRecordService.list();
        }
        return ApiResponse.success(responses);
    }
}
