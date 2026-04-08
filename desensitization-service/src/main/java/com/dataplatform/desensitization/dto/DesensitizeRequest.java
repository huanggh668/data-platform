package com.dataplatform.desensitization.dto;

import com.dataplatform.desensitization.model.SensitiveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesensitizeRequest {
    private String value;
    private SensitiveType type;
}
