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
public class EncryptionJobResponse {
    private Long id;
    private String name;
    private Long algorithmId;
    private String sourcePath;
    private String targetPath;
    private String operation;
    private String status;
    private Integer progress;
    private String errorMessage;
    private String cronExpression;
    private Boolean isScheduled;
    private LocalDateTime executeAt;
    private LocalDateTime completedAt;
    private String result;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}