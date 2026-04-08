package com.dataplatform.encryption.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyInfo {
    private String keyId;
    private AlgorithmType algorithm;
    private LocalDateTime createdAt;
    private String description;
}