package com.dataplatform.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String email;
    private String phone;
}
