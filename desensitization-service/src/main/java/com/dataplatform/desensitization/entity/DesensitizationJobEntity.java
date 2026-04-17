package com.dataplatform.desensitization.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataplatform.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("DESENSITIZATION_JOB")
public class DesensitizationJobEntity extends BaseEntity {

    private String name;

    private Long sourceId;

    private Long targetId;

    private String ruleIds;

    private String status;

    /** 任务执行进度 0-100，执行中实时更新 */
    private Integer progress;

    /** 任务错误信息，执行失败时写入 */
    private String errorMessage;

    private String cronExpression;

    private Boolean isScheduled;

    private LocalDateTime executeAt;

    private LocalDateTime completedAt;

    private String result;
}
