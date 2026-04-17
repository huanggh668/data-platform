package com.dataplatform.watermark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.watermark.entity.WatermarkJobEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WatermarkJobMapper extends BaseMapper<WatermarkJobEntity> {
}