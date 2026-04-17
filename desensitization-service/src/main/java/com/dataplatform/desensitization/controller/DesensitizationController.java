package com.dataplatform.desensitization.controller;

import com.dataplatform.desensitization.dto.BatchDesensitizeRequest;
import com.dataplatform.desensitization.dto.BatchDesensitizeResponse;
import com.dataplatform.desensitization.dto.DesensitizeRequest;
import com.dataplatform.desensitization.dto.DesensitizeResponse;
import com.dataplatform.desensitization.entity.DesensitizationLog;
import com.dataplatform.desensitization.model.DetectionResult;
import com.dataplatform.desensitization.service.DesensitizationService;
import com.dataplatform.desensitization.service.SensitiveDataDetector;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "数据脱敏核心", description = "单条/批量脱敏、敏感数据检测及操作日志查询")
@RestController
@RequestMapping("/api/v1/desensitize")
public class DesensitizationController {

    private final DesensitizationService desensitizationService;
    private final SensitiveDataDetector detector;

    public DesensitizationController(DesensitizationService desensitizationService, SensitiveDataDetector detector) {
        this.desensitizationService = desensitizationService;
        this.detector = detector;
    }

    @Operation(summary = "单条数据脱敏", description = "对指定类型（phone/email/idcard/custom）的单条数据进行脱敏")
    @PostMapping
    public ResponseEntity<DesensitizeResponse> desensitize(@RequestBody DesensitizeRequest request) {
        String original = request.getValue();
        String desensitized = desensitizationService.desensitize(original, request.getType());
        DesensitizeResponse response = new DesensitizeResponse(original, desensitized, request.getType());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "自动检测并脱敏", description = "对 Map 中的所有字段自动识别敏感类型并脱敏")
    @PostMapping("/auto")
    public ResponseEntity<Map<String, Object>> autoDesensitize(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = desensitizationService.autoDesensitize(data);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "检测敏感数据类型", description = "对输入字段进行敏感类型识别，返回检测结果列表")
    @PostMapping("/detect")
    public ResponseEntity<List<DetectionResult>> detect(@RequestBody Map<String, Object> data) {
        List<DetectionResult> allResults = new ArrayList<>();
        for (Object value : data.values()) {
            if (value instanceof String) {
                allResults.addAll(detector.detectAll((String) value));
            }
        }
        return ResponseEntity.ok(allResults);
    }

    @Operation(summary = "批量脱敏", description = "传入记录列表，对每条记录自动脱敏后返回")
    @PostMapping("/batch")
    public ResponseEntity<BatchDesensitizeResponse> batchDesensitize(@RequestBody BatchDesensitizeRequest request) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (request.getRecords() != null) {
            for (Map<String, Object> record : request.getRecords()) {
                results.add(desensitizationService.autoDesensitize(record));
            }
        }
        return ResponseEntity.ok(new BatchDesensitizeResponse(results));
    }

    @Operation(summary = "查询脱敏日志", description = "可按数据类型过滤，默认返回最近 100 条")
    @GetMapping("/logs")
    public ResponseEntity<List<DesensitizationLog>> queryLogs(
            @Parameter(description = "数据类型（可选）") @RequestParam(required = false) String dataType) {
        List<DesensitizationLog> logs;
        if (dataType != null && !dataType.isEmpty()) {
            logs = desensitizationService.queryLogsByDataType(dataType);
        } else {
            logs = desensitizationService.queryLogs(1, 100);
        }
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "根据ID查询脱敏日志")
    @GetMapping("/logs/{id}")
    public ResponseEntity<DesensitizationLog> getLogById(
            @Parameter(description = "日志ID") @PathVariable Long id) {
        DesensitizationLog log = desensitizationService.getLogById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }
}
