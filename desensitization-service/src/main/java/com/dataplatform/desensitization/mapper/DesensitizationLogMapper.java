package com.dataplatform.desensitization.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.desensitization.entity.DesensitizationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DesensitizationLogMapper extends BaseMapper<DesensitizationLog> {
}