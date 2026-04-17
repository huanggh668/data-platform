package com.dataplatform.encryption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.encryption.entity.EncryptionJobEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EncryptionJobMapper extends BaseMapper<EncryptionJobEntity> {
}