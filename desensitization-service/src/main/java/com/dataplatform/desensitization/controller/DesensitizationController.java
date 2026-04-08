package com.dataplatform.desensitization.controller;

import com.dataplatform.desensitization.dto.BatchDesensitizeRequest;
import com.dataplatform.desensitization.dto.BatchDesensitizeResponse;
import com.dataplatform.desensitization.dto.DesensitizeRequest;
import com.dataplatform.desensitization.dto.DesensitizeResponse;
import com.dataplatform.desensitization.entity.DesensitizationLog;
import com.dataplatform.desensitization.model.DetectionResult;
import com.dataplatform.desensitization.service.DesensitizationService;
import com.dataplatform.desensitization.service.SensitiveDataDetector;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/desensitize")
public class DesensitizationController {

    private final DesensitizationService desensitizationService;
    private final SensitiveDataDetector detector;

    public DesensitizationController(DesensitizationService desensitizationService, SensitiveDataDetector detector) {
        this.desensitizationService = desensitizationService;
        this.detector = detector;
    }

    @PostMapping
    public ResponseEntity<DesensitizeResponse> desensitize(@RequestBody DesensitizeRequest request) {
        String original = request.getValue();
        String desensitized = desensitizationService.desensitize(original, request.getType());
        DesensitizeResponse response = new DesensitizeResponse(original, desensitized, request.getType());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auto")
    public ResponseEntity<Map<String, Object>> autoDesensitize(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = desensitizationService.autoDesensitize(data);
        return ResponseEntity.ok(result);
    }

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

    @GetMapping("/logs")
    public ResponseEntity<List<DesensitizationLog>> queryLogs(
            @RequestParam(required = false) String dataType) {
        List<DesensitizationLog> logs;
        if (dataType != null && !dataType.isEmpty()) {
            logs = desensitizationService.queryLogsByDataType(dataType);
        } else {
            logs = desensitizationService.queryLogs(1, 100);
        }
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<DesensitizationLog> getLogById(@PathVariable Long id) {
        DesensitizationLog log = desensitizationService.getLogById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }
}
