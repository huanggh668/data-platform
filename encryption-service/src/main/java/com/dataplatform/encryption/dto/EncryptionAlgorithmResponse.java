package com.dataplatform.encryption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionAlgorithmResponse {
    private Long id;
    private String name;
    private String type;
    private Integer keyLength;
    private String mode;
    private String padding;
    private String config;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}