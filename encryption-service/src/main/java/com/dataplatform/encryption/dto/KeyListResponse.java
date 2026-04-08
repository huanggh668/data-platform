package com.dataplatform.encryption.dto;

import com.dataplatform.encryption.model.KeyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyListResponse {
    private List<KeyInfo> keys;
}