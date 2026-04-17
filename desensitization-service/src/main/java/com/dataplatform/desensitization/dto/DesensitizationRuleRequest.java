package com.dataplatform.desensitization.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DesensitizationRuleRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String dataType;

    private String pattern;

    private String replacement;

    private Integer priority;

    private Boolean enabled;
}
