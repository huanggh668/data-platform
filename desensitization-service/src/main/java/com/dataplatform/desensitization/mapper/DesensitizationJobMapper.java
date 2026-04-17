package com.dataplatform.desensitization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.desensitization.entity.DesensitizationJobEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DesensitizationJobMapper extends BaseMapper<DesensitizationJobEntity> {
}
