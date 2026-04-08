package com.dataplatform.watermark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbedTextRequest {
    private String text;
    private Long userId;
    private String dataType;
}
