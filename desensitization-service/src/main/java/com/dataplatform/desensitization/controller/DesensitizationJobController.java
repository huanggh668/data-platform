package com.dataplatform.desensitization.controller;

import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.desensitization.dto.DesensitizationJobRequest;
import com.dataplatform.desensitization.dto.DesensitizationJobResponse;
import com.dataplatform.desensitization.service.DesensitizationJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "脱敏任务管理", description = "脱敏任务的创建、执行、进度查询等接口")
@RestController
@RequestMapping("/api/v1/desensitization/jobs")
@RequiredArgsConstructor
public class DesensitizationJobController {

    private final DesensitizationJobService desensitizationJobService;

    @Operation(summary = "创建脱敏任务")
    @PostMapping
    public ResponseEntity<ApiResponse<DesensitizationJobResponse>> create(
            @RequestBody DesensitizationJobRequest request) {
        DesensitizationJobResponse response = desensitizationJobService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created", response));
    }

    @Operation(summary = "更新脱敏任务")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesensitizationJobResponse>> update(
            @Parameter(description = "任务ID") @PathVariable Long id,
            @RequestBody DesensitizationJobRequest request) {
        DesensitizationJobResponse response = desensitizationJobService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Job updated", response));
    }

    @Operation(summary = "删除脱敏任务（软删除）")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        desensitizationJobService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Job deleted", null));
    }

    @Operation(summary = "根据ID查询脱敏任务")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DesensitizationJobResponse>> getById(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        DesensitizationJobResponse response = desensitizationJobService.getById(id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Job not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "查询所有脱敏任务")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DesensitizationJobResponse>>> listAll() {
        List<DesensitizationJobResponse> list = desensitizationJobService.listAll();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "执行脱敏任务", description = "触发任务立即执行，任务状态变为 RUNNING")
    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<DesensitizationJobResponse>> execute(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        DesensitizationJobResponse response = desensitizationJobService.execute(id);
        return ResponseEntity.ok(ApiResponse.success("Job execution started", response));
    }

    @Operation(summary = "查询任务执行进度", description = "返回任务当前进度（0-100）、状态及错误信息")
    @GetMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<DesensitizationJobResponse>> getProgress(
            @Parameter(description = "任务ID") @PathVariable Long id) {
        DesensitizationJobResponse response = desensitizationJobService.getById(id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Job not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
