package com.dataplatform.desensitization.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dataplatform.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("DATA_SOURCE")
public class DataSource extends BaseEntity {

    private String name;

    private String type;

    private String config;
}
