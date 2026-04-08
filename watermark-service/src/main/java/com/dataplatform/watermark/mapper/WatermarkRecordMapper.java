package com.dataplatform.watermark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.watermark.model.WatermarkRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WatermarkRecordMapper extends BaseMapper<WatermarkRecord> {
}