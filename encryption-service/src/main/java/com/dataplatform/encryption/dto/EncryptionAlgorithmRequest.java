package com.dataplatform.encryption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionAlgorithmRequest {
    private String name;
    private String type;
    private Integer keyLength;
    private String mode;
    private String padding;
    private String config;
}