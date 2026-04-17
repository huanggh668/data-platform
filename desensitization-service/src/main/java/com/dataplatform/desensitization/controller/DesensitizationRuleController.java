package com.dataplatform.desensitization.controller;

import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.desensitization.dto.DesensitizationRuleRequest;
import com.dataplatform.desensitization.dto.DesensitizationRuleResponse;
import com.dataplatform.desensitization.service.DesensitizationRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "脱敏规则管理", description = "脱敏规则的增删改查及启用/禁用接口")
@RestController
@RequestMapping("/api/v1/desensitization/rules")
@RequiredArgsConstructor
public class DesensitizationRuleController {

    private final DesensitizationRuleService desensitizationRuleService;

    @Operation(summary = "创建脱敏规则")
    @PostMapping
    public ResponseEntity<ApiResponse<DesensitizationRuleResponse>> create(
            @RequestBody DesensitizationRuleRequest request) {
        DesensitizationRuleResponse response = desensitizationRuleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rule created", response));
    }

    @Operation(summary = "更新脱敏规则")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesensitizationRuleResponse>> update(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @RequestBody DesensitizationRuleRequest request) {
        DesensitizationRuleResponse response = desensitizationRuleService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Rule updated", response));
    }

    @Operation(summary = "删除脱敏规则")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        desensitizationRuleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Rule deleted", null));
    }

    @Operation(summary = "根据ID查询规则")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DesensitizationRuleResponse>> getById(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        DesensitizationRuleResponse response = desensitizationRuleService.getById(id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Rule not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "查询所有规则")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DesensitizationRuleResponse>>> listAll() {
        List<DesensitizationRuleResponse> list = desensitizationRuleService.listAll();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "查询所有启用规则")
    @GetMapping("/enabled")
    public ResponseEntity<ApiResponse<List<DesensitizationRuleResponse>>> listEnabled() {
        List<DesensitizationRuleResponse> list = desensitizationRuleService.listEnabled();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
