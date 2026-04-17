package com.dataplatform.encryption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dataplatform.encryption.entity.EncryptionAlgorithmEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EncryptionAlgorithmMapper extends BaseMapper<EncryptionAlgorithmEntity> {
}