package com.dataplatform.watermark.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatermarkData implements Serializable {
    
    private Long userId;
    private Long timestamp;
    private String dataType;
    private String signature;
    
    public String toEncodedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(userId != null ? userId : "null");
        sb.append("|");
        sb.append(timestamp != null ? timestamp : "null");
        sb.append("|");
        sb.append(dataType != null ? dataType : "null");
        sb.append("|");
        sb.append(signature != null ? signature : "null");
        return sb.toString();
    }
    
    public static WatermarkData fromEncodedString(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }
        String[] parts = encoded.split("\\|");
        if (parts.length < 3) {
            return null;
        }
        return WatermarkData.builder()
                .userId("null".equals(parts[0]) ? null : Long.parseLong(parts[0]))
                .timestamp("null".equals(parts[1]) ? null : Long.parseLong(parts[1]))
                .dataType("null".equals(parts[2]) ? null : parts[2])
                .signature(parts.length > 3 && !"null".equals(parts[3]) ? parts[3] : null)
                .build();
    }
}
