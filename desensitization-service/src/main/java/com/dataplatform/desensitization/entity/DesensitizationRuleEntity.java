package com.dataplatform.desensitization.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataplatform.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("DESENSITIZATION_RULE")
public class DesensitizationRuleEntity extends BaseEntity {

    private String name;

    private String dataType;

    private String pattern;

    private String replacement;

    private Integer priority;

    private Boolean enabled;
}
