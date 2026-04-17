package com.dataplatform.encryption.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.encryption.dto.EncryptionJobRequest;
import com.dataplatform.encryption.dto.EncryptionJobResponse;
import com.dataplatform.encryption.service.EncryptionJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "加密任务管理", description = "加密/解密任务的创建、执行及进度查询接口")
@RestController
@RequestMapping("/api/v1/encryption/jobs")
@RequiredArgsConstructor
public class EncryptionJobController {

    private final EncryptionJobService encryptionJobService;

    @Operation(summary = "创建加密任务")
    @PostMapping
    public ApiResponse<EncryptionJobResponse> create(@RequestBody EncryptionJobRequest request) {
        EncryptionJobResponse response = encryptionJobService.create(request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "更新加密任务")
    @PutMapping("/{id}")
    public ApiResponse<EncryptionJobResponse> update(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestBody EncryptionJobRequest request) {
        EncryptionJobResponse response = encryptionJobService.update(id, request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "删除加密任务")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "任务ID") @PathVariable Long id) {
        encryptionJobService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "根据ID查询加密任务")
    @GetMapping("/{id}")
    public ApiResponse<EncryptionJobResponse> getById(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        EncryptionJobResponse response = encryptionJobService.getById(id);
        if (response == null) {
            return ApiResponse.notFound("Job not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "分页查询加密任务", description = "page 从 1 开始，size 默认 10")
    @GetMapping
    public ApiResponse<IPage<EncryptionJobResponse>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        IPage<EncryptionJobResponse> result = encryptionJobService.page(page, size);
        return ApiResponse.success(result);
    }

    @Operation(summary = "执行加密任务", description = "触发任务立即执行，任务状态变为 RUNNING")
    @PostMapping("/{id}/execute")
    public ApiResponse<EncryptionJobResponse> execute(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        EncryptionJobResponse response = encryptionJobService.execute(id);
        return ApiResponse.success(response);
    }

    @Operation(summary = "查询任务执行进度", description = "返回任务当前进度（0-100）、状态及错误信息")
    @GetMapping("/{id}/progress")
    public ApiResponse<EncryptionJobResponse> getProgress(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        EncryptionJobResponse response = encryptionJobService.getById(id);
        if (response == null) {
            return ApiResponse.notFound("Job not found");
        }
        return ApiResponse.success(response);
    }
}
