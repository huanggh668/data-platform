package com.dataplatform.desensitization.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataSourceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String type;

    private String config;
}
