package com.dataplatform.encryption.dto;

import com.dataplatform.encryption.model.AlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptResponse {
    private String ciphertext;
    private AlgorithmType algorithm;
}