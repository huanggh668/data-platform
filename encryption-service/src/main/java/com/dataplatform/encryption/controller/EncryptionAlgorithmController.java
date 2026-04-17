package com.dataplatform.encryption.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.encryption.dto.EncryptionAlgorithmRequest;
import com.dataplatform.encryption.dto.EncryptionAlgorithmResponse;
import com.dataplatform.encryption.service.EncryptionAlgorithmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "加密算法管理", description = "加密算法配置的增删改查及分页查询接口")
@RestController
@RequestMapping("/api/v1/encryption/algorithms")
@RequiredArgsConstructor
public class EncryptionAlgorithmController {

    private final EncryptionAlgorithmService encryptionAlgorithmService;

    @Operation(summary = "创建加密算法配置")
    @PostMapping
    public ApiResponse<EncryptionAlgorithmResponse> create(@RequestBody EncryptionAlgorithmRequest request) {
        EncryptionAlgorithmResponse response = encryptionAlgorithmService.create(request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "更新加密算法配置")
    @PutMapping("/{id}")
    public ApiResponse<EncryptionAlgorithmResponse> update(
            @Parameter(description = "算法ID") @PathVariable Long id,
            @RequestBody EncryptionAlgorithmRequest request) {
        EncryptionAlgorithmResponse response = encryptionAlgorithmService.update(id, request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "删除加密算法配置")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@Parameter(description = "算法ID") @PathVariable Long id) {
        encryptionAlgorithmService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "根据ID查询加密算法")
    @GetMapping("/{id}")
    public ApiResponse<EncryptionAlgorithmResponse> getById(
            @Parameter(description = "算法ID") @PathVariable Long id) {
        EncryptionAlgorithmResponse response = encryptionAlgorithmService.getById(id);
        if (response == null) {
            return ApiResponse.notFound("Algorithm not found");
        }
        return ApiResponse.success(response);
    }

    @Operation(summary = "分页查询加密算法", description = "page 从 1 开始，size 默认 10")
    @GetMapping
    public ApiResponse<IPage<EncryptionAlgorithmResponse>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        IPage<EncryptionAlgorithmResponse> result = encryptionAlgorithmService.page(page, size);
        return ApiResponse.success(result);
    }
}
