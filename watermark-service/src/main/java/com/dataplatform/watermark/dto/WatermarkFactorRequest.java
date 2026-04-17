package com.dataplatform.watermark.dto;

import lombok.Data;

@Data
public class WatermarkFactorRequest {
    private String name;
    private String type;
    private String value;
}