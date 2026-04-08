package com.dataplatform.watermark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbedImageRequest {
    private String imageBase64;
    private Long userId;
    private String dataType;
}
