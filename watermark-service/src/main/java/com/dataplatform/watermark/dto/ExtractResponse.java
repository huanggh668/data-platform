package com.dataplatform.watermark.dto;

import com.dataplatform.watermark.model.WatermarkData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractResponse {
    private WatermarkData watermarkInfo;
}
