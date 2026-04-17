package com.dataplatform.watermark.controller;

import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.watermark.dto.WatermarkFactorRequest;
import com.dataplatform.watermark.dto.WatermarkFactorResponse;
import com.dataplatform.watermark.service.WatermarkFactorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "水印因子管理", description = "水印因子的增删改查接口，因子用于水印嵌入时的溯源标识")
@RestController
@RequestMapping("/api/v1/watermark/factors")
@RequiredArgsConstructor
public class WatermarkFactorController {
    private final WatermarkFactorService watermarkFactorService;

    @Operation(summary = "创建水印因子")
    @PostMapping
    public ApiResponse<WatermarkFactorResponse> create(@RequestBody WatermarkFactorRequest request) {
        WatermarkFactorResponse response = watermarkFactorService.create(request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "更新水印因子")
    @PutMapping("/{id}")
    public ApiResponse<WatermarkFactorResponse> update(
            @Parameter(description = "因子ID") @PathVariable Long id,
            @RequestBody WatermarkFactorRequest request) {
        WatermarkFactorResponse response = watermarkFactorService.update(id, request);
        if (response == null) {
            return ApiResponse.notFound("Factor not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "删除水印因子")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "因子ID") @PathVariable Long id) {
        watermarkFactorService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "根据ID查询水印因子")
    @GetMapping("/{id}")
    public ApiResponse<WatermarkFactorResponse> getById(
            @Parameter(description = "因子ID") @PathVariable Long id) {
        WatermarkFactorResponse response = watermarkFactorService.getById(id);
        if (response == null) {
            return ApiResponse.notFound("Factor not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "查询所有水印因子")
    @GetMapping
    public ApiResponse<List<WatermarkFactorResponse>> list() {
        List<WatermarkFactorResponse> responses = watermarkFactorService.list();
        return ApiResponse.success(responses);
    }
}
