package com.dataplatform.desensitization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResult {
    private String fieldName;
    private String value;
    private SensitiveType sensitiveType;
    private int startIndex;
    private int endIndex;
}
