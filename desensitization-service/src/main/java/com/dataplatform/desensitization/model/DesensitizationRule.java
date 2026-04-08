package com.dataplatform.desensitization.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesensitizationRule {
    private SensitiveType sensitiveType;
    private int prefixLength;
    private int suffixLength;
    private char maskChar;
    private String replacement;

    public DesensitizationRule(SensitiveType sensitiveType, int prefixLength, int suffixLength, char maskChar) {
        this.sensitiveType = sensitiveType;
        this.prefixLength = prefixLength;
        this.suffixLength = suffixLength;
        this.maskChar = maskChar;
        this.replacement = null;
    }
}
