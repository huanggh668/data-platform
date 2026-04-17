package com.dataplatform.desensitization.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesensitizationJobResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long sourceId;
    private Long targetId;
    private String ruleIds;
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
