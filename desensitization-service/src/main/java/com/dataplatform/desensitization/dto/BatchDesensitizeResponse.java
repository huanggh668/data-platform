package com.dataplatform.desensitization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchDesensitizeResponse {
    private List<Map<String, Object>> results;
}
