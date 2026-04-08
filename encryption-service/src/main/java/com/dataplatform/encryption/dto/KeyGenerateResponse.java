package com.dataplatform.encryption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyGenerateResponse {
    private String keyId;
    private String publicKey;
    private String privateKey;
}