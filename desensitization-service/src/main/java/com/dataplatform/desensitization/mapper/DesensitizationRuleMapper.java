package com.dataplatform.desensitization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.desensitization.entity.DesensitizationRuleEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DesensitizationRuleMapper extends BaseMapper<DesensitizationRuleEntity> {
}
