package com.dataplatform.desensitization.controller;

import com.dataplatform.common.dto.ApiResponse;
import com.dataplatform.desensitization.dto.DataSourceRequest;
import com.dataplatform.desensitization.dto.DataSourceResponse;
import com.dataplatform.desensitization.service.DataSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "数据源管理", description = "脱敏服务数据源的增删改查接口")
@RestController
@RequestMapping("/api/v1/datasources")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceService dataSourceService;

    @Operation(summary = "创建数据源")
    @PostMapping
    public ResponseEntity<ApiResponse<DataSourceResponse>> create(@RequestBody DataSourceRequest request) {
        DataSourceResponse response = dataSourceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Data source created", response));
    }

    @Operation(summary = "更新数据源")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DataSourceResponse>> update(
            @Parameter(description = "数据源ID") @PathVariable Long id,
            @RequestBody DataSourceRequest request) {
        DataSourceResponse response = dataSourceService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Data source updated", response));
    }

    @Operation(summary = "删除数据源")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "数据源ID") @PathVariable Long id) {
        dataSourceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Data source deleted", null));
    }

    @Operation(summary = "根据ID查询数据源")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DataSourceResponse>> getById(
            @Parameter(description = "数据源ID") @PathVariable Long id) {
        DataSourceResponse response = dataSourceService.getById(id);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Data source not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "查询所有数据源")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DataSourceResponse>>> listAll() {
        List<DataSourceResponse> list = dataSourceService.listAll();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
