package com.dataplatform.watermark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.watermark.entity.WatermarkTraceRecordEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WatermarkTraceRecordMapper extends BaseMapper<WatermarkTraceRecordEntity> {
}