package com.dataplatform.watermark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractRequest {
    private String data;
    private String dataBase64;
    private String type;
}
