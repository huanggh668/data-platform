package com.dataplatform.desensitization.dto;

import com.dataplatform.desensitization.model.SensitiveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesensitizeResponse {
    private String original;
    private String desensitized;
    private SensitiveType type;
}
