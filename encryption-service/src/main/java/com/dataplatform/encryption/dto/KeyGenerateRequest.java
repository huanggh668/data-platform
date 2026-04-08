package com.dataplatform.encryption.dto;

import com.dataplatform.encryption.model.AlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyGenerateRequest {
    private AlgorithmType algorithm;
}