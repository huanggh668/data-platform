package com.dataplatform.encryption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionJobRequest {
    private String name;
    private Long algorithmId;
    private String sourcePath;
    private String targetPath;
    private String operation;
    private String cronExpression;
    private Boolean isScheduled;
}