package com.dataplatform.desensitization.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DesensitizationJobRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Long sourceId;

    private Long targetId;

    private String ruleIds;

    private String cronExpression;

    private Boolean isScheduled;
}
